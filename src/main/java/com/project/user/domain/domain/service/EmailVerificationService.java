package com.project.user.domain.domain.service;

import com.project.user.domain.domain.repository.VerificationCodeRepository;
import com.project.user.domain.infra.helper.EmailTemplateHelper;
import com.project.user.domain.infra.smtp.SmtpEmailSender;
import com.project.user.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.project.user.global.exception.code.status.AuthErrorStatus.FAILED_EMAIL_VERIFICATION;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final SmtpEmailSender smtpEmailSender;

    @Async
    public void sendCode(String email, String code) {
        verificationCodeRepository.save(email, code);
        var template = EmailTemplateHelper.generate(code);

        smtpEmailSender.send(email, template.subject(), template.body());
    }

    public void verifyCode(String email, String code) {
        verificationCodeRepository.findByEmail(email)
                .filter(code::equals)
                .orElseThrow(() -> new RestApiException(FAILED_EMAIL_VERIFICATION));

        verificationCodeRepository.delete(email);
    }
}
