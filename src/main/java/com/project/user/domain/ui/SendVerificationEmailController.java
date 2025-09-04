package com.project.user.domain.ui;

import com.project.user.domain.application.usecase.SendVerificationEmailUseCase;
import com.project.user.domain.ui.spec.SendVerificationEmailApiSpec;
import com.project.user.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SendVerificationEmailController implements SendVerificationEmailApiSpec {

    private final SendVerificationEmailUseCase sendVerificationEmailUseCase;

    @Override
    public BaseResponse<Void> send(
            @RequestParam String email
    ) {
        sendVerificationEmailUseCase.send(email);
        return BaseResponse.onSuccess();
    }
}
