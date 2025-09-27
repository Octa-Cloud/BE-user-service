package com.project.user.domain.ui;

import com.project.user.domain.application.dto.request.SleepGoalRequest;
import com.project.user.domain.application.dto.response.SleepGoalResponse;
import com.project.user.domain.application.usecase.GetSleepGoalUseCase;
import com.project.user.domain.application.usecase.SetSleepGoalUseCase;
import com.project.user.global.annotation.CurrentUser;
import com.project.user.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/goals")
@RequiredArgsConstructor
@Tag(name = "수면 목표", description = "수면 목표 관련 API")
public class SleepGoalController {

    private final GetSleepGoalUseCase getSleepGoalUseCase;
    private final SetSleepGoalUseCase setSleepGoalUseCase;

    @GetMapping
    @Operation(summary = "수면 목표 조회", description = "사용자의 수면 목표를 조회합니다.")
    public BaseResponse<SleepGoalResponse> getSleepGoal(
            @CurrentUser Long userNo
    ) {
        SleepGoalResponse response = getSleepGoalUseCase.getSleepGoal(userNo);
        return BaseResponse.onSuccess(response);
    }

    @PostMapping
    @Operation(summary = "수면 목표 설정/갱신", description = "사용자의 수면 목표를 생성하거나 갱신합니다.")
    public BaseResponse<SleepGoalResponse> setSleepGoal(
            @CurrentUser Long userNo,
            @Valid @RequestBody SleepGoalRequest request
    ) {
        SleepGoalResponse response = setSleepGoalUseCase.setSleepGoal(userNo, request);
        return BaseResponse.onSuccess(response);
    }
}