package com.project.user.domain.domain.service;

import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.repository.UserRepository;
import com.project.user.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.project.user.global.exception.code.status.AuthErrorStatus.PASSWORD_MISMATCH;
import static com.project.user.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserDeletionService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void delete(Long userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));

        // 비밀번호 해싱을 사용한 검증
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RestApiException(PASSWORD_MISMATCH);
        }

        userRepository.delete(user);
    }
}