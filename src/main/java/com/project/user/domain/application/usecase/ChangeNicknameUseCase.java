package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.request.ChangeNicknameRequest;
import com.project.user.domain.application.dto.response.ChangeNicknameResponse;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeNicknameUseCase {

    private final UserService userService;

    public ChangeNicknameResponse changeNickname(Long userId, ChangeNicknameRequest request){

        User updated = userService.updateNickname(userId, request);
        return ChangeNicknameResponse.from(updated);
    }

}
