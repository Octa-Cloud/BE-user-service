package com.project.user.domain.ui.spec;

import com.project.user.domain.application.dto.request.ChangePasswordRequest;
import com.project.user.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/users/profile")
@Tag(name = "User")
public interface ChangePasswordApiSpec {

    @Operation(
            summary = "비밀번호 변경 API",
            description = "사용자가 변경하고자 하는 비밀번호를 입력해 기존 비밀번호를 변경합니다."
    )
    @PatchMapping("/password")
    BaseResponse<String> changePassword(
            Long userNo,
            @RequestBody(
                    description = "변경하고자 하는 비밀번호",
                    required = true
            ) ChangePasswordRequest request


    );
}
