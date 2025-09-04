package com.project.user.domain.application.usecase;

import com.project.user.domain.domain.service.EmailVerificationService;
import com.project.user.domain.domain.service.VerifiedEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerifyEmailUseCase {

    private final EmailVerificationService emailVerificationService;
    private final VerifiedEmailService verifiedEmailService;

    public void execute(String email, String code) {
        emailVerificationService.verifyCode(email, code);
        verifiedEmailService.save(email);
    }
}
