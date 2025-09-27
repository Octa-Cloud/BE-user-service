package com.project.user.domain.application.dto.response;

import com.project.user.domain.domain.entity.User;
import lombok.Builder;

@Builder
public record ChangePasswordResponse(
    String password
) {
    public static ChangePasswordResponse from(User user) {
        return ChangePasswordResponse.builder()
                .password(user.getPassword())
                .build();
    }
}
