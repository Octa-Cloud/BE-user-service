package com.project.user.domain.domain.entity;

import com.project.user.global.common.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@Table(name = "sleep_goals")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SleepGoal extends BaseEntity {

    @Id @Tsid
    @Column(name = "goal_no")
    private Long goalNo;

    @Column(name = "user_no")
    private Long userNo;

    @Column(name = "goal_bed_time")
    private LocalTime goalBedTime;

    @Column(name = "goal_wake_time")
    private LocalTime goalWakeTime;

    @Column(name = "goal_total_sleep_time")
    private Integer goalTotalSleepTime;

    public void update(LocalTime goalBedTime, LocalTime goalWakeTime, Integer goalTotalSleepTime) {
        this.goalBedTime = goalBedTime;
        this.goalWakeTime = goalWakeTime;
        this.goalTotalSleepTime = goalTotalSleepTime;
    }
}