package com.project.user.domain.domain.service;

import com.project.user.global.exception.RestApiException;
import com.project.user.global.properties.JwtProperties;
import com.project.user.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static com.project.user.global.exception.code.status.AuthErrorStatus.EXPIRED_MEMBER_JWT;
import static com.project.user.global.exception.code.status.AuthErrorStatus.INVALID_REFRESH_TOKEN;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenReissueService {

    private final TokenProvider tokenProvider;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public TokenReissueResponse reissue(String refreshToken, Long userNo) {
        if (!refreshTokenService.isExist(refreshToken, userNo))
            throw new RestApiException(INVALID_REFRESH_TOKEN);

        refreshTokenService.deleteRefreshToken(userNo);

        User user = userService.findById(userNo);
        String newAccessToken = tokenProvider.createToken(userNo, jwtProperties.getAccessTokenSubject());
        String newRefreshToken = tokenProvider.createToken(userNo, jwtProperties.getRefreshTokenSubject());
        Duration duration = tokenProvider.getRemainingDuration(refreshToken)
                .orElseThrow(() -> new RestApiException(EXPIRED_MEMBER_JWT));

        refreshTokenService.saveRefreshToken(userNo, newRefreshToken, duration);

        return new TokenReissueResponse(newAccessToken, newRefreshToken);
    }
}
