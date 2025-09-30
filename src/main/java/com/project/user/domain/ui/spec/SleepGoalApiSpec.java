package com.project.user.domain.ui.spec;

import com.project.user.domain.application.dto.request.SleepGoalRequest;
import com.project.user.domain.application.dto.response.SleepGoalResponse;
import com.project.user.global.annotation.CurrentUser;
import com.project.user.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Sleep Goal")
public interface SleepGoalApiSpec {

    @GetMapping("/api/users/goals")
    @Operation(summary = "수면 목표 조회", description = "사용자의 수면 목표를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = SleepGoalResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "GOAL4001: 해당 유저의 수면 목표가 존재하지 않습니다.")
    })
    BaseResponse<SleepGoalResponse> getSleepGoal(
            @Parameter(hidden = true) @CurrentUser Long userNo
    );

    @PostMapping("/api/users/goals")
    @Operation(summary = "수면 목표 설정/갱신", description = "사용자의 수면 목표를 생성하거나 갱신합니다.")
    BaseResponse<SleepGoalResponse> setSleepGoal(
            @Parameter(hidden = true) @CurrentUser Long userNo,
            @Valid @RequestBody SleepGoalRequest request
    );
}