package com.project.user.domain.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserDeletionRequest (
        @NotBlank String password
) {}