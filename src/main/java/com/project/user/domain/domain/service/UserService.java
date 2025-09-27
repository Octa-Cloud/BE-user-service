package com.project.user.domain.domain.service;

import com.project.user.domain.application.dto.request.ChangeNicknameRequest;
import com.project.user.domain.application.dto.request.ChangePasswordRequest;
import com.project.user.domain.application.dto.request.SignUpRequest;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.repository.UserRepository;
import com.project.user.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.project.user.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));
    }

    public boolean isAlreadyRegistered(String email) {
        return userRepository.existsByEmail(email);
    }

    public User save(SignUpRequest request) {
        return userRepository.save(
                User.builder()
                        .email(request.email())
                        .name(request.name())
                        .nickname(request.nickname())
                        .password(passwordEncoder.encode(request.password()))
                        .gender(request.gender())
                        .build()
        );
    }

    public User findById(Long userNo) {
        return userRepository.findById(userNo)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));
    }

    public void validateExistsById(Long userNo) {
        if (!userRepository.existsById(userNo))
            throw new RestApiException(_NOT_FOUND);
    }

    public User updateNickname(Long userNo, ChangeNicknameRequest request) {
        User user = findById(userNo);
        user.changeNickname(request.nickname());
        return userRepository.save(user);
    }

    public User updatePassword(Long userNo, ChangePasswordRequest request) {
        User user = findById(userNo);
        user.changePassword(passwordEncoder.encode(request.password()));
        return userRepository.save(user);
    }
}
