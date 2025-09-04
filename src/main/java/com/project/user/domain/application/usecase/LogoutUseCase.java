package com.project.user.domain.application.usecase;

import com.project.user.domain.domain.service.RefreshTokenService;
import com.project.user.domain.domain.service.TokenBlacklistService;
import com.project.user.domain.domain.service.TokenWhitelistService;
import com.project.user.global.exception.RestApiException;
import com.project.user.global.security.TokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.project.user.global.exception.code.status.AuthErrorStatus.INVALID_ACCESS_TOKEN;
import static com.project.user.global.exception.code.status.AuthErrorStatus.INVALID_ID_TOKEN;

@Service
@Transactional
@RequiredArgsConstructor
public class LogoutUseCase {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final TokenWhitelistService tokenWhitelistService;

    public void execute(String accessToken) {
        Long userNo = tokenProvider.getId(accessToken)
                .orElseThrow(() -> new RestApiException(INVALID_ID_TOKEN));

        Duration expiration = tokenProvider.getRemainingDuration(accessToken)
                .orElseThrow(() -> new RestApiException(INVALID_ACCESS_TOKEN));

        tokenWhitelistService.deleteWhitelistToken(accessToken);

        refreshTokenService.deleteRefreshToken(userNo);
        tokenBlacklistService.blacklist(accessToken, expiration);
    }
}
