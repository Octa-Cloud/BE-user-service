package com.project.user.domain.application.dto.response;

import java.time.LocalTime;

public record SummaryResponse (

        Byte score,
        int sleepTime,
        LocalTime bedTime
){ }
