package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.request.LoginRequest;
import com.project.user.domain.application.dto.response.LoginResponse;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.service.RefreshTokenService;
import com.project.user.domain.domain.service.UserService;
import com.project.user.global.exception.RestApiException;
import com.project.user.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.project.user.global.exception.code.status.AuthErrorStatus.EXPIRED_MEMBER_JWT;
import static com.project.user.global.exception.code.status.AuthErrorStatus.LOGIN_ERROR;


@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    public LoginResponse execute(LoginRequest request) {
        User user = userService.findByEmail(request.email());

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new RestApiException(LOGIN_ERROR);

        String accessToken = tokenProvider.createAccessToken(user.getUserNo());
        String refreshToken = tokenProvider.createRefreshToken(user.getUserNo());

        Duration tokenExpiration = tokenProvider.getRemainingDuration(refreshToken)
                .orElseThrow(() -> new RestApiException(EXPIRED_MEMBER_JWT));
        refreshTokenService.saveRefreshToken(user.getUserNo(), refreshToken, tokenExpiration);

        return new LoginResponse(accessToken, refreshToken);
    }
}
