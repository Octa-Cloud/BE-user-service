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
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String,String> kafka;
    private final ObjectMapper om;

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
     * - @RetryableTopic: 최대 5회 지수 백오프 재시도 → 실패 시 user.final.delete.dlt 로 이동
     * - InvalidPayloadException 은 exclude → 즉시 DLT(재시도 없음)
     * - 수신 → 파싱/검증(독성은 즉시 DLT) → 멱등 시작 → 비즈니스(하드 삭제) → reply 동기 전송
     * - 커밋 이후에만 수동 ACK(MANUAL_IMMEDIATE)로 오프셋 커밋 → DB/메시지 정합 보장
     */
    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "true",
            dltTopicSuffix = ".dlt",
            exclude = { InvalidPayloadException.class } // ★ 독성 페이로드는 즉시 DLT
    )
    @KafkaListener(
            topics = "user.final.delete",
            groupId = "user-service",
            containerFactory = "kafkaManualAckFactory"
    )
    @Transactional
    public void onFinalDelete(ConsumerRecord<String,String> rec, Acknowledgment ack) throws Exception {

        // 0) 파싱 + 유효성 검증 — 독성(InvalidPayloadException)은 즉시 DLT 보내도록 로깅 후 재던짐
        final JsonNode n;
        try {
            n = om.readTree(rec.value());
            String eventId = n.path("eventId").asText("").trim();
            long userNo    = n.path("userNo").asLong(0L);
            if (eventId.isEmpty()) throw new InvalidPayloadException("Missing or empty 'eventId'");
            if (userNo <= 0L)      throw new InvalidPayloadException("Invalid 'userNo'");

            // 1) 멱등 처리(이미 성공이면 스킵)
            if (!tryBegin(eventId, "FINAL_DELETE")) {
                ack.acknowledge();
                return;
            }

            try {
                // 2) 실제 하드 삭제(멱등)
                userRepo.deleteById(userNo);

                // 3) 성공 reply 동기 전송(acks=all, .get())
                var reply = om.createObjectNode()
                        .put("eventId", eventId)
                        .put("userNo", userNo)
                        .put("status", "SUCCESS")
                        .put("type", "FINAL_DELETE");
                kafka.send("user.final.reply", String.valueOf(userNo), om.writeValueAsString(reply)).get();

                // 운영 관측성: 키/이벤트/유저
                log.info("FINAL_DELETE ok key={}, eventId={}, userNo={}", rec.key(), eventId, userNo);

                // 4) 성공 마킹
                markSuccess(eventId);

                // 5) 트랜잭션 커밋 이후 ACK (정합성 보장)
                org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                        new org.springframework.transaction.support.TransactionSynchronization() {
                            @Override public void afterCommit() { ack.acknowledge(); }
                        }
                );
            } catch (Exception ex) {
                log.warn("FINAL_DELETE failed userNo={}, eventId={}, err={}", userNo, eventId, ex.toString());
                markError(eventId, ex.getMessage());
                throw ex; // 재시도 → 최대 횟수 후 DLT
            }

        } catch (InvalidPayloadException bad) {
            // ★ 독성 페이로드 → 재시도 없이 즉시 DLT (exclude 규칙)
            log.error("[user-service] toxic payload -> DLT, value={}", rec.value());
            throw bad;
        }
    }

    @KafkaListener(topics = "user.final.delete.dlt", groupId = "user-service")
    public void onFinalDeleteDlt(String payload){
        // DLT 모니터링(알람/대시보드 연계 지점)
        log.error("[DLT][user.final.delete] {}", payload);
    }
}