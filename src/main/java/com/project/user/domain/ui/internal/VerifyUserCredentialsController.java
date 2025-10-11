package com.project.user.domain.ui.internal;

import com.project.user.domain.application.dto.request.VerifyCredentialRequest;
import com.project.user.domain.application.usecase.VerifyUserCredentialsUseCase;
import com.project.user.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VerifyUserCredentialsController {

    private final VerifyUserCredentialsUseCase verifyUserCredentialsUseCase;

    @PostMapping("/api/users/internal/credentials")
    public BaseResponse<Long> verify(
            @RequestBody VerifyCredentialRequest request
    ) {
        return BaseResponse.onSuccess(verifyUserCredentialsUseCase.execute(request));
    }
}
