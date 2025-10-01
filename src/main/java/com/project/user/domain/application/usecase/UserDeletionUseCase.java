package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.request.UserDeletionRequest;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.service.RefreshTokenService;
import com.project.user.domain.domain.service.TokenBlacklistService;
import com.project.user.domain.domain.service.UserService;
import com.project.user.global.exception.RestApiException;
import com.project.user.global.security.TokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.project.user.global.exception.code.status.AuthErrorStatus.INVALID_ACCESS_TOKEN;
import static com.project.user.global.exception.code.status.AuthErrorStatus.INVALID_ID_TOKEN;
import static com.project.user.global.exception.code.status.AuthErrorStatus.INVALID_PASSWORD;

@Service
@Transactional
@RequiredArgsConstructor
public class UserDeletionUseCase {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public void execute(String accessToken, UserDeletionRequest request) {
        Long userNo = tokenProvider.getId(accessToken)
                .orElseThrow(() -> new RestApiException(INVALID_ID_TOKEN));

        User user = userService.findById(userNo);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RestApiException(INVALID_PASSWORD);
        }

        Duration expiration = tokenProvider.getRemainingDuration(accessToken)
                .orElseThrow(() -> new RestApiException(INVALID_ACCESS_TOKEN));

        refreshTokenService.deleteRefreshToken(userNo);
        tokenBlacklistService.blacklist(accessToken, expiration);
        userService.deleteUser(userNo);
    }
}