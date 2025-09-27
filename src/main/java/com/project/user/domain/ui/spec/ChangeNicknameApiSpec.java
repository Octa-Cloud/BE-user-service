package com.project.user.domain.ui.spec;


import com.project.user.domain.application.dto.request.ChangeNicknameRequest;
import com.project.user.domain.application.dto.response.ChangeNicknameResponse;
import com.project.user.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User")
public interface ChangeNicknameApiSpec {

    @Operation(
            summary = "닉네임 변경 API",
            description = "사용자가 변경하고자 하는 닉네임을 입력해 기존 닉네임을 변경합니다."
    )

    BaseResponse<ChangeNicknameResponse> changeNickname(
            @Parameter(hidden = true, description = "JWT에서 추출된 사용자 ID") Long userId,
            @Parameter(description = "변경하고자 하는 닉네임")ChangeNicknameRequest request
            );
}
