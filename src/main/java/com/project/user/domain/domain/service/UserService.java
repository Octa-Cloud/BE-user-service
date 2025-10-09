package com.project.user.domain.domain.service;

import com.project.user.domain.application.dto.request.SignUpRequest;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.repository.UserRepository;
import com.project.user.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.user.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
                        .password(BCrypt.hashpw(request.password(), BCrypt.gensalt(12)))
                        .gender(request.gender())
                        .build()
        );
    }

    public User findById(Long userNo) {
        return userRepository.findById(userNo)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));
    }

    @Transactional
    public void deleteUser(Long userNo) {
        userRepository.deleteById(userNo);
    }
}
