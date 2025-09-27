package com.project.user.domain.domain.entity;

import com.project.user.global.common.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id @Tsid
    @Column(name = "user_no")
    private Long userNo;

    private String name;

    private String nickname;

    @Column(nullable = false)
    private String email;

    private LocalDate birth;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    @Min(0) @Max(100)
    @Column(name = "avg_score", columnDefinition = "TINYINT UNSIGNED")
    private Integer avgScore;

    @Column(name = "avg_sleep_time")
    private Integer avgSleepTime;

    @Column(name = "avg_bed_time")
    private LocalTime avgBedTime;

    @PrePersist
    public void prePersist() {
        if(avgScore == null) {
            avgScore = 0;
        }
        if(avgSleepTime == null) {
            avgSleepTime = 0;
        }
        if(avgBedTime == null) {
            avgBedTime = LocalTime.of(23, 0);
        }
    }
}
