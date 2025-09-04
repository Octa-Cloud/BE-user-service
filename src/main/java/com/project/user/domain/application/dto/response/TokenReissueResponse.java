package com.project.user.domain.application.dto.response;

public record TokenReissueResponse (
        String accessToken,
        String refreshToken
) {}
