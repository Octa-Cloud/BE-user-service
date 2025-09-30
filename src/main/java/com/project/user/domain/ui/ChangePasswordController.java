package com.project.user.domain.ui;

import com.project.user.domain.application.dto.request.ChangePasswordRequest;
import com.project.user.domain.application.usecase.ChangePasswordUseCase;
import com.project.user.domain.ui.spec.ChangePasswordApiSpec;
import com.project.user.global.annotation.CurrentUser;
import com.project.user.global.common.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChangePasswordController implements ChangePasswordApiSpec {

    private final ChangePasswordUseCase changePasswordUseCase;

    @Override
    public BaseResponse<String> changePassword(
            @CurrentUser Long userNo,
            @Valid @RequestBody ChangePasswordRequest request
            ){

        changePasswordUseCase.changePassword(userNo, request);
        return BaseResponse.onSuccess("비밀번호가 성공적으로 변경됐습니다.");

        }
}
