package com.project.user.domain.application.dto.response;

import java.time.LocalTime;

public record SummaryResponse (

        int score,
        int sleepTime,
        LocalTime bedTime
){ }
