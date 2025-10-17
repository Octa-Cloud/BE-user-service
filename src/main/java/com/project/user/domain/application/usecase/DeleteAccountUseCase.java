package com.project.user.domain.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.user.domain.application.dto.request.UserDeletionRequest;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.service.UserService;
import com.project.user.global.exception.RestApiException;
import com.project.user.global.security.TokenProvider;
import com.project.user.domain.infra.kafka.ResilientSender;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

import static com.project.user.global.exception.code.status.AuthErrorStatus.*;
import static com.project.user.global.exception.code.status.AuthErrorStatus.INVALID_ID_TOKEN;
import static com.project.user.global.exception.code.status.AuthErrorStatus.INVALID_PASSWORD;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeleteAccountUseCase {

    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final ObjectMapper om;
    private final ResilientSender sender;
    /**
     * 유저 삭제 흐름의 시작점.
     * 1) 액세스 토큰에서 userNo 추출 및 사용자 검증
     * 2) 토큰 TTL(초) 계산 → 오케스트레이터가 블랙리스트 TTL로 사용
     * 3) user.deletion.start 주제에 사가 시작 이벤트를 "동기 전송(get)"으로 발행
     *    - acks=all + .get() 으로 브로커 수신을 확인(전송 멱등/안정성 강화)
     * 컨트롤러는 202 Accepted로 비동기 진행 알림이 자연스럽다.
     */
    public void execute(String accessToken, UserDeletionRequest request) {
        log.debug("accessToken userId={}", tokenProvider.getId(accessToken));
        Long userNo = tokenProvider.getId(accessToken)
                .orElseThrow(() -> new RestApiException(INVALID_ID_TOKEN));

        User user = userService.findById(userNo);

        if (!BCrypt.checkpw(request.password(), user.getPassword())) {
            throw new RestApiException(INVALID_PASSWORD);
        }

        Duration expiration = tokenProvider.getRemainingDuration(accessToken)
                .orElseThrow(() -> new RestApiException(INVALID_ACCESS_TOKEN));
        long ttlSec = Math.max(0, expiration.getSeconds());

        var start = new StartEvent(
                UUID.randomUUID().toString(), // ★ sagaId == correlationId
                userNo,
                accessToken,
                ttlSec
        );

        try {
            String payload = om.writeValueAsString(start);
            // key=userNo 로 파티셔닝 → 동일 유저 이벤트 순서 보장
            //kafka.send("user.start-delete.command", String.valueOf(userNo), payload).get();
            sender.sendSync("user.start-delete.command", String.valueOf(userNo), payload);
        } catch (Exception e) {
            // 프로듀스 실패 → 트랜잭션 롤백(서비스 계층 예외로 래핑)
            throw new RuntimeException("failed to publish user.start-delete.command", e);
        }
    }

    // 오케스트레이터가 소비하는 시작 이벤트 포맷
    public record StartEvent(String sagaId, Long userNo, String accessToken, long accessTokenTtlSec) {}
}