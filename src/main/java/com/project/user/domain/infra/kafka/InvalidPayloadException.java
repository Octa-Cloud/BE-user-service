package com.project.user.domain.infra.kafka;

public class InvalidPayloadException extends RuntimeException {
    // ★ 파싱/검증 불가(독성)인 경우 던져서 @RetryableTopic exclude 로 즉시 DLT 이동
    public InvalidPayloadException(String message) { super(message); }
    public InvalidPayloadException(String message, Throwable cause) { super(message, cause); }
}