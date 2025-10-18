package com.project.user.domain.domain.service;

import com.project.user.domain.application.dto.request.ChangeNicknameRequest;
import com.project.user.domain.application.dto.request.ChangePasswordRequest;
import com.project.user.domain.application.dto.request.SignUpRequest;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.repository.UserRepository;
import com.project.user.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.project.user.global.exception.code.status.GlobalErrorStatus.PASSWORD_NOT_MATCH;
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

    public User updateNickname(Long userNo, ChangeNicknameRequest request) {
        User user = findById(userNo);
        user.changeNickname(request.nickname());
        return user;
    }

    public void updatePassword(Long userNo, ChangePasswordRequest request) {
        //비밀번호 == 확인용 비밀번호 검사, 문자열 값 자체 비교
        if(!Objects.equals(request.password(), request.checkPassword())){
            throw new RestApiException(PASSWORD_NOT_MATCH);
        }
        User user = findById(userNo);
        user.changePassword(BCrypt.hashpw(request.password(), BCrypt.gensalt(12)));
    }
    @Transactional
    public void deleteUser(Long userNo) {
        // 멱등: 이미 삭제돼 있어도 예외 없이 통과
        userRepository.findById(userNo).ifPresent(userRepository::delete);
    }
}
