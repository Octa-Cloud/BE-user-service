package com.project.user.domain.infra.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.user.domain.domain.repository.UserRepository;
import com.project.user.domain.domain.entity.ProcessedEvent;
import com.project.user.domain.domain.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFinalDeleteListener {

    private final UserRepository userRepo;
    private final ProcessedEventRepository peRepo;
    private final ObjectMapper om;
    private final ResilientSender sender;
    /** 멱등 시작 마킹: (없으면 INSERT) → 이미 SUCCESS면 스킵 */
    private boolean tryBegin(String eventId, String type){
        if (!peRepo.existsById(eventId)) {
            try {
                peRepo.save(ProcessedEvent.builder()
                        .eventId(eventId).type(type).status("IN_PROGRESS").attempts(1).build());
                return true;
            } catch (org.springframework.dao.DataIntegrityViolationException dup) {
                // 경쟁 INSERT(동일 키) → 아래 로직으로 이어감
            }
        }
        var pe = peRepo.findById(eventId).orElse(null);
        return pe == null || !"SUCCESS".equals(pe.getStatus());
    }
    private void markSuccess(String eventId){
        var pe = peRepo.findById(eventId).orElse(null);
        if (pe != null){
            pe.setStatus("SUCCESS");
            pe.setUpdatedAt(java.time.Instant.now());
            peRepo.save(pe);
        }
    }
    private void markError(String eventId, String msg){
        var pe = peRepo.findById(eventId).orElse(null);
        if (pe != null){
            pe.setStatus("ERROR");
            pe.setAttempts(pe.getAttempts()+1);
            pe.setLastError(msg);
            pe.setUpdatedAt(java.time.Instant.now());
            peRepo.save(pe);
        }
    }

    /**
     * 최종 삭제 처리 플로우:
     * - @RetryableTopic: 최대 5회 지수 백오프 재시도 → 실패 시 user.final-delete.command.dlt 로 이동
     * - InvalidPayloadException 은 exclude → 즉시 DLT(재시도 없음)
     * - 수신 → 파싱/검증(독성은 즉시 DLT) → 멱등 시작 → 비즈니스(하드 삭제) → reply 동기 전송
     * - 커밋 이후에만 수동 ACK(MANUAL_IMMEDIATE)로 오프셋 커밋 → DB/메시지 정합 보장
     */
    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "false",
            dltTopicSuffix = ".dlt",
            exclude = { InvalidPayloadException.class }
    )
    @KafkaListener(
            topics = "user.final-delete.command",
            groupId = "user-service",
            containerFactory = "kafkaManualAckFactory"
    )
    @Transactional
    public void onFinalDelete(ConsumerRecord<String,String> rec, Acknowledgment ack) throws Exception {

        // 0) 파싱 + 유효성 검증 — 독성(InvalidPayloadException)은 즉시 DLT
        final JsonNode n;
        try {
            n = om.readTree(rec.value());
        } catch (Exception e) {
            log.error("[user-service] toxic payload -> DLT, value={}", rec.value());
            throw new InvalidPayloadException("Invalid JSON: " + e.getMessage(), e);
        }

        final String eventId = n.path("eventId").asText("").trim();
        final long userNo    = n.path("userNo").asLong(0L);
        if (eventId.isEmpty()) throw new InvalidPayloadException("Missing or empty 'eventId'");
        if (userNo <= 0L)      throw new InvalidPayloadException("Invalid 'userNo'");

        // 1) 멱등 처리(이미 SUCCESS면 스킵)
        if (!tryBegin(eventId, "FINAL_DELETE")) {
            ack.acknowledge();
            return;
        }

        // 2) 실제 하드 삭제(멱등)
        try {
            userRepo.deleteById(userNo);

            // 3) 성공 마킹(DB 커밋 대상)
            markSuccess(eventId);

            // 4) 커밋 이후 reply 전송 + ACK
            final String replyPayload = om.createObjectNode()
                    .put("eventId", eventId)
                    .put("userNo", userNo)
                    .put("status", "SUCCESS")
                    .put("type", "FINAL_DELETE")
                    .toString();

            org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                    new org.springframework.transaction.support.TransactionSynchronization() {
                        @Override public void afterCommit() {
                            try {
                                // 커밋 확정 후에만 브로커에 회신(정합성 ↑)
                                sender.sendSync("user.final-delete.reply", String.valueOf(userNo), replyPayload);
                                ack.acknowledge(); // 전송 성공시에만 오프셋 커밋
                                log.info("FINAL_DELETE ok key={}, eventId={}, userNo={}", rec.key(), eventId, userNo);
                            } catch (Exception sendEx) {
                                // 전송 실패: ACK 하지 않음 → 오프셋 미커밋 상태 유지
                                // 컨슈머 재시작/리밸런스 시 같은 레코드 재처리됨(@RetryableTopic와는 별개)
                                log.error("FINAL_DELETE reply send failed (will be retried on re-consume). " +
                                        "eventId={}, userNo={}, err={}", eventId, userNo, sendEx.toString());
                            }
                        }
                    }
            );

        } catch (Exception ex) {
            // 비즈니스/기술 예외 → 재시도/DTL 체인
            log.warn("FINAL_DELETE failed userNo={}, eventId={}, err={}", userNo, eventId, ex.toString());
            markError(eventId, ex.getMessage());
            throw ex;
        }
    }
}
