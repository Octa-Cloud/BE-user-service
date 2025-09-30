package com.project.user.domain.ui;

import com.project.user.domain.application.dto.request.SleepGoalRequest;
import com.project.user.domain.application.dto.response.SleepGoalResponse;
import com.project.user.domain.application.usecase.GetSleepGoalUseCase;
import com.project.user.domain.application.usecase.SetSleepGoalUseCase;
import com.project.user.domain.ui.spec.SleepGoalApiSpec;
import com.project.user.global.annotation.CurrentUser;
import com.project.user.global.common.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SleepGoalController implements SleepGoalApiSpec {

    private final GetSleepGoalUseCase getSleepGoalUseCase;
    private final SetSleepGoalUseCase setSleepGoalUseCase;

    @Override
    public BaseResponse<SleepGoalResponse> getSleepGoal(
            @CurrentUser Long userNo
    ) {
        SleepGoalResponse response = getSleepGoalUseCase.getSleepGoal(userNo);
        return BaseResponse.onSuccess(response);
    }

    @Override
    public BaseResponse<SleepGoalResponse> setSleepGoal(
            @CurrentUser Long userNo,
            @Valid @RequestBody SleepGoalRequest request
    ) {
        SleepGoalResponse response = setSleepGoalUseCase.setSleepGoal(userNo, request);
        return BaseResponse.onSuccess(response);
    }
}