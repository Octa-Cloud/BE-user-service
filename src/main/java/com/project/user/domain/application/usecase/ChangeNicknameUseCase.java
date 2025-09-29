package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.request.ChangeNicknameRequest;
import com.project.user.domain.application.dto.response.ChangeNicknameResponse;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangeNicknameUseCase {

    private final UserService userService;
    @Transactional
    public ChangeNicknameResponse changeNickname(Long userNo, ChangeNicknameRequest request){

        User updated = userService.updateNickname(userNo, request);
        return ChangeNicknameResponse.from(updated);
    }

}
