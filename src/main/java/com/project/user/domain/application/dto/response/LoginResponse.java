package com.project.user.domain.application.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
