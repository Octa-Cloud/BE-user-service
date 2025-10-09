package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.request.UserDeletionRequest;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.service.UserService;
import com.project.user.global.exception.RestApiException;
import com.project.user.global.security.TokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import static com.project.user.global.exception.code.status.AuthErrorStatus.INVALID_ID_TOKEN;
import static com.project.user.global.exception.code.status.AuthErrorStatus.INVALID_PASSWORD;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteAccountUseCase {

    private final TokenProvider tokenProvider;
    private final UserService userService;

    public void execute(String accessToken, UserDeletionRequest request) {
        Long userNo = tokenProvider.getId(accessToken)
                .orElseThrow(() -> new RestApiException(INVALID_ID_TOKEN));

        User user = userService.findById(userNo);

        if (!BCrypt.checkpw(request.password(), user.getPassword())) {
            throw new RestApiException(INVALID_PASSWORD);
        }

        // todo: Kafka - blacklist 전파

        userService.deleteUser(userNo);
    }
}