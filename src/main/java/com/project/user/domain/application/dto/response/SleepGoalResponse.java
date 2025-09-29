package com.project.user.domain.application.dto.response;

import com.project.user.domain.domain.entity.SleepGoal;
import lombok.Builder;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
public record SleepGoalResponse(
        Long userNo,
        LocalTime goalBedTime,
        LocalTime goalWakeTime,
        Integer goalTotalSleepTime,
        LocalDateTime updatedAt
) {
    public static SleepGoalResponse from(SleepGoal sleepGoal) {
        return SleepGoalResponse.builder()
                .userNo(sleepGoal.getUserNo())
                .goalBedTime(sleepGoal.getGoalBedTime())
                .goalWakeTime(sleepGoal.getGoalWakeTime())
                .goalTotalSleepTime(sleepGoal.getGoalTotalSleepTime())
                .updatedAt(sleepGoal.getUpdatedAt())
                .build();
    }
}