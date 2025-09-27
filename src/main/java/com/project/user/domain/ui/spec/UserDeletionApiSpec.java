package com.project.user.domain.ui.spec;

import com.project.user.domain.application.dto.request.UserDeletionRequest;
import com.project.user.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;

@Tag(name = "User")
public interface UserDeletionApiSpec {

    @Operation(
            summary = "회원탈퇴 API",
            description = "사용자가 회원탈퇴를 진행합니다. userId와 비밀번호를 검증하여 사용자를 삭제합니다."
    )
    @DeleteMapping("/api/users")
    BaseResponse<String> deleteUser(
            @RequestBody(
                    description = "회원탈퇴 요청 바디 (userId, 비밀번호)",
                    required = true
            )
            UserDeletionRequest request
    );
}