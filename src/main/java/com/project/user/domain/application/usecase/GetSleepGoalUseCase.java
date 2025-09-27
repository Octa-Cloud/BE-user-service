package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.response.SleepGoalResponse;
import com.project.user.domain.domain.entity.SleepGoal;
import com.project.user.domain.domain.service.SleepGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetSleepGoalUseCase {

    private final SleepGoalService sleepGoalService;

    public SleepGoalResponse getSleepGoal(Long userNo) {
        SleepGoal sleepGoal = sleepGoalService.getSleepGoal(userNo);
        return SleepGoalResponse.from(sleepGoal);
    }
}