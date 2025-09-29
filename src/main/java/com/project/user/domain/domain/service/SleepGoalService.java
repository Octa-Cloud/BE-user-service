package com.project.user.domain.domain.service;

import com.project.user.domain.application.dto.request.SleepGoalRequest;
import com.project.user.domain.domain.entity.SleepGoal;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.repository.SleepGoalRepository;
import com.project.user.domain.domain.repository.UserRepository;
import com.project.user.global.exception.RestApiException;
import com.project.user.global.exception.code.status.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SleepGoalService {

    private final SleepGoalRepository sleepGoalRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public SleepGoal getSleepGoal(Long userNo) {
        return sleepGoalRepository.findByUserNo(userNo)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus.SLEEP_GOAL_NOT_FOUND));
    }

    @Transactional
    public SleepGoal setSleepGoal(Long userNo, SleepGoalRequest request) {
        SleepGoal sleepGoal = sleepGoalRepository.findByUserNo(userNo)
                .map(goal -> {
                    // 목표가 이미 있으면, 기존 엔티티의 값을 업데이트합니다.
                    goal.update(
                            request.goalBedTime(),
                            request.goalWakeTime(),
                            request.goalTotalSleepTime()
                    );
                    return goal;
                })
                .orElseGet(() -> {
                    // 목표가 없으면, 새로 생성합니다.
                    User user = userRepository.findById(userNo)
                            .orElseThrow(() -> new RestApiException(GlobalErrorStatus.USER_NOT_FOUND));

                    return SleepGoal.builder()
                            .userNo(userNo)
                            .goalBedTime(request.goalBedTime())
                            .goalWakeTime(request.goalWakeTime())
                            .goalTotalSleepTime(request.goalTotalSleepTime())
                            .build();
                });

        return sleepGoalRepository.save(sleepGoal);
    }
}