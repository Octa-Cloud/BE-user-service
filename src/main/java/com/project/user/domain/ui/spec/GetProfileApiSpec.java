package com.project.user.domain.ui.spec;

import com.project.user.domain.application.dto.response.GetProfileResponse;
import com.project.user.global.annotation.CurrentUser;
import com.project.user.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "User")
public interface GetProfileApiSpec {
    @Operation(
            summary = "회원 정보 조회",
            description = "사용자의 이름, 이메일, 닉네임, 생년월일, 성별을 반환합니다."
    )
    @GetMapping("api/users/information")
    BaseResponse<GetProfileResponse> information(
            @CurrentUser @Parameter(hidden = true) Long userNo
    );
}
