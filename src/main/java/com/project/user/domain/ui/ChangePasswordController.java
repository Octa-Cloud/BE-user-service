package com.project.user.domain.ui;

import com.project.user.domain.application.dto.request.ChangePasswordRequest;
import com.project.user.domain.application.dto.response.ChangePasswordResponse;
import com.project.user.domain.application.usecase.ChangePasswordUseCase;
import com.project.user.domain.ui.spec.ChangePasswordApiSpec;
import com.project.user.global.annotation.CurrentUser;
import com.project.user.global.common.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
public class ChangePasswordController implements ChangePasswordApiSpec {

    private final ChangePasswordUseCase changePasswordUseCase;

    @Override
    @PatchMapping("/password")
    public BaseResponse<ChangePasswordResponse> changePassword(
            @CurrentUser Long userNo,
            @Valid @RequestBody ChangePasswordRequest request
            ){
        return BaseResponse.onSuccess(changePasswordUseCase.changePassword(userNo,request));

        }
}
