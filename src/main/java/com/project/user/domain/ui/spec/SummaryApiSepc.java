package com.project.user.domain.ui.spec;

import com.project.user.domain.application.dto.response.SummaryResponse;
import com.project.user.global.annotation.CurrentUser;
import com.project.user.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "User")
public interface SummaryApiSepc {

    @Operation(
            summary = "전체 평균 데이터 조회",
            description = "전체 평균 점수, 수면시간, 취침 시간을 반환합니다."
    )
    @GetMapping("/api/user/summary")
    BaseResponse<SummaryResponse> summary(
            @CurrentUser @Parameter(hidden = true) Long userNo
    );
}
