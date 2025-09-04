package com.project.user.domain.application.usecase;

import com.project.user.domain.domain.service.EmailVerificationService;
import com.project.user.domain.domain.service.UserService;
import com.project.user.global.exception.RestApiException;
import com.project.user.global.util.SecureRandomGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.project.user.global.exception.code.status.AuthErrorStatus.ALREADY_REGISTERED_EMAIL;

@Service
@RequiredArgsConstructor
public class SendVerificationEmailUseCase {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;
    private final SecureRandomGenerator secureRandomGenerator;

    public void send(String email) {
        if (userService.isAlreadyRegistered(email))
            throw new RestApiException(ALREADY_REGISTERED_EMAIL);

        String code = secureRandomGenerator.generate();
        emailVerificationService.sendCode(email, code);
    }
}
