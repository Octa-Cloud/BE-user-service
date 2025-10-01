package com.project.user.domain.ui.spec;


import com.project.user.domain.application.dto.request.ChangeNicknameRequest;
import com.project.user.domain.application.dto.response.ChangeNicknameResponse;
import com.project.user.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/users/profile")
@Tag(name = "User")
public interface ChangeNicknameApiSpec {

    @Operation(
            summary = "닉네임 변경 API",
            description = "사용자가 변경하고자 하는 닉네임을 입력해 기존 닉네임을 변경합니다."
    )

    @PatchMapping("/nickname")
    BaseResponse<ChangeNicknameResponse> changeNickname(
            Long userNo,
            @RequestBody(
                    description = "변경하고자 하는 닉네임",
                    required = true
            ) ChangeNicknameRequest request

    );
}
