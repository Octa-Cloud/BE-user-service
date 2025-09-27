package com.project.user.domain.application.dto.response;

import com.project.user.domain.domain.entity.Gender;

import java.time.LocalDate;

public record GetProfileResponse(
        String name,
        String email,
        String nickname,
        LocalDate birth,
        Gender gender
){ }
