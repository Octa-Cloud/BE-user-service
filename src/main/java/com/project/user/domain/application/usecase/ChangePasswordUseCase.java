package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.request.ChangePasswordRequest;
import com.project.user.domain.application.dto.response.ChangePasswordResponse;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangePasswordUseCase {
    private final UserService userService;

    public ChangePasswordResponse changePassword(Long userId, ChangePasswordRequest request){

        User updated = userService.updatePassword(userId, request);
        return ChangePasswordResponse.from(updated);
    }
}
