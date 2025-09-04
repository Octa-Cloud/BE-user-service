package com.project.user.domain.ui;

import com.project.user.domain.application.usecase.VerifyEmailUseCase;
import com.project.user.domain.ui.spec.VerifyEmailApiSpec;
import com.project.user.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VerifyEmailController implements VerifyEmailApiSpec {

    private final VerifyEmailUseCase verifyEmailUseCase;

    @Override
    public BaseResponse<Void> verifyEmail(
            @RequestParam String email,
            @RequestParam String code
    ) {
        verifyEmailUseCase.execute(email, code);
        return BaseResponse.onSuccess();
    }
}
