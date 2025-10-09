package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.request.VerifyCredentialRequest;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.service.UserService;
import com.project.user.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import static com.project.user.global.exception.code.status.AuthErrorStatus.LOGIN_ERROR;

@Service
@RequiredArgsConstructor
public class VerifyUserCredentialsUseCase {

    private final UserService userService;

    public Long execute(VerifyCredentialRequest request) {
        User user = userService.findByEmail(request.email());

        if (!BCrypt.checkpw(request.password(), user.getPassword())) {
            throw new RestApiException(LOGIN_ERROR);
        }

        return user.getUserNo();
    }
}
