package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.response.SummaryResponse;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummaryUseCase {
    private final UserService userService;

    public SummaryResponse execute(Long userNo) {
        User user = userService.findById(userNo);
        return new SummaryResponse(
                user.getAvgScore(),
                user.getAvgSleepTime(),
                user.getAvgBedTime());
    }
}
