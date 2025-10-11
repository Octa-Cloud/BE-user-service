package com.project.user.domain.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyCredentialRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {}
