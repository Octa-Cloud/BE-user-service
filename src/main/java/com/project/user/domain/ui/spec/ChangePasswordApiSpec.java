package com.project.user.domain.ui.spec;

import com.project.user.domain.application.dto.request.ChangePasswordRequest;
import com.project.user.domain.application.dto.response.ChangePasswordResponse;
import com.project.user.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User")
public interface ChangePasswordApiSpec {

    @Operation(
            summary = "비밀번호 변경 API",
            description = "사용자가 변경하고자 하는 비밀번호를 입력해 기존 비밀번호를 변경합니다."
    )
    BaseResponse<ChangePasswordResponse> changePassword(
            @Parameter(hidden = true,description = "JWT에서 추출된 사용자 ID") Long userId,
            @Parameter(description = "변경하고자 하는 비밀번호") ChangePasswordRequest request
    );
}
