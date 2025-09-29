package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.request.SleepGoalRequest;
import com.project.user.domain.application.dto.response.SleepGoalResponse;
import com.project.user.domain.domain.entity.SleepGoal;
import com.project.user.domain.domain.service.SleepGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SetSleepGoalUseCase {

    private final SleepGoalService sleepGoalService;

    public SleepGoalResponse setSleepGoal(Long userNo, SleepGoalRequest request) {
        SleepGoal savedGoal = sleepGoalService.setSleepGoal(userNo, request);
        return SleepGoalResponse.from(savedGoal);
    }
}
