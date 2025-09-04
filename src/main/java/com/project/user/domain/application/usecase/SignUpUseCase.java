package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.request.SignUpRequest;
import com.project.user.domain.domain.service.UserService;
import com.project.user.domain.domain.service.VerifiedEmailService;
import com.project.user.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.project.user.global.exception.code.status.AuthErrorStatus.ALREADY_REGISTERED_EMAIL;
import static com.project.user.global.exception.code.status.AuthErrorStatus.NOT_VERIFIED_EMAIL;

@Service
@RequiredArgsConstructor
public class SignUpUseCase {

    private final UserService userService;
    private final VerifiedEmailService verifiedEmailService;

    public void execute(SignUpRequest request) {
        if (userService.isAlreadyRegistered(request.email()))
            throw new RestApiException(ALREADY_REGISTERED_EMAIL);

        if (!verifiedEmailService.isExist(request.email()))
            throw new RestApiException(NOT_VERIFIED_EMAIL);

        verifiedEmailService.delete(request.email());
        userService.save(request);
    }
}
