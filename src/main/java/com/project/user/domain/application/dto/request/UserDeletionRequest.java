package com.project.user.domain.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDeletionRequest(
        @NotNull Long userId,
        @NotBlank String password
) {}