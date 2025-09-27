package com.project.user.domain.application.dto.response;

import java.time.LocalTime;

public record SummaryResponse (

        Integer score,
        Integer sleepTime,
        LocalTime bedTime
){ }
