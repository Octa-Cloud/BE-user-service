package com.project.user.domain.application.dto.request;

import com.project.user.domain.domain.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SignUpRequest(
        @NotBlank String name,
        @NotBlank LocalDate birth,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotNull Gender gender
) {}
