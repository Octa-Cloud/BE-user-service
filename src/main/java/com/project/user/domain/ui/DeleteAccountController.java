package com.project.user.domain.ui;

import com.project.user.domain.application.dto.request.UserDeletionRequest;
import com.project.user.domain.application.usecase.DeleteAccountUseCase;
import com.project.user.domain.ui.spec.DeleteAccountApiSpec;
import com.project.user.global.annotation.AccessToken;
import com.project.user.global.annotation.CurrentUser;
import com.project.user.global.common.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeleteAccountController implements DeleteAccountApiSpec {

    private final DeleteAccountUseCase deleteAccountUseCase;

    @Override
    public BaseResponse<Void> deleteUser(
            @CurrentUser Long userNo,
            @AccessToken String accessToken,
            @RequestBody @Valid UserDeletionRequest request
    ) {
        deleteAccountUseCase.execute(accessToken, request);
        return BaseResponse.onSuccess();
    }
}