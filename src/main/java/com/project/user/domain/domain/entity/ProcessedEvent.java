package com.project.user.domain.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name="processed_event")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProcessedEvent {
    /**
     * eventId 를 PK로 사용 → 동일 이벤트 재처리 방지(멱등).
     * 상태 전이가 명확: IN_PROGRESS → SUCCESS | ERROR(+attempts++).
     */
    @Id
    @Column(length=36)
    private String eventId;

    @Column(nullable=false, length=16)
    private String type;   // FINAL_DELETE

    @Column(nullable=false, length=16)
    private String status; // IN_PROGRESS | SUCCESS | ERROR

    @Builder.Default
    @Column(nullable=false)
    private Integer attempts = 1;

    private String lastError;

    @Builder.Default
    @Column(nullable=false)
    private Instant updatedAt = Instant.now();

    // DB 기본값 보장(INSERT/UPDATE 시각)
    @PrePersist
    public void prePersist() {
        if (attempts == null) attempts = 1;
        if (updatedAt == null) updatedAt = Instant.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}