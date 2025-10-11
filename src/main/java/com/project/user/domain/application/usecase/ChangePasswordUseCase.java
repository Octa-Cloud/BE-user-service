package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.request.ChangePasswordRequest;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.service.UserService;
import com.project.user.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.user.global.exception.code.status.GlobalErrorStatus.PASSWORD_NOT_MATCH;

@Service
@RequiredArgsConstructor
public class ChangePasswordUseCase {

    private final UserService userService;

    @Transactional
    public void changePassword(Long userNo, ChangePasswordRequest request){
        if(!request.password().equals(request.checkPassword()))
            throw new RestApiException(PASSWORD_NOT_MATCH);

        User user = userService.findById(userNo);
        user.changePassword(BCrypt.hashpw(request.password(), BCrypt.gensalt(12)));
    }
}
