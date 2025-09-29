package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.request.ChangePasswordRequest;
import com.project.user.domain.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangePasswordUseCase {
    private final UserService userService;

    @Transactional
    public void changePassword(Long userNo, ChangePasswordRequest request){
        userService.updatePassword(userNo, request);
    }
}
