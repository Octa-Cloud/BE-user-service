package com.project.user.domain.domain.service;

import com.project.user.domain.application.dto.request.ChangeNicknameRequest;
import com.project.user.domain.application.dto.request.ChangePasswordRequest;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.repository.UserRepository;
import com.project.user.global.exception.RestApiException;
import com.project.user.global.exception.code.status.GlobalErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    private Long userNo;

    @BeforeEach
    void setUp() { userNo = 42L; }

    // --- updateNickname ---

    @Test
    void updateUserWhenNicknameValid() {
        // Given
        User user = User.builder().nickname("old").build();
        ChangeNicknameRequest req = new ChangeNicknameRequest("newNick");
        when(userRepository.findById(userNo)).thenReturn(Optional.of(user));

        // When
        User updated = userService.updateNickname(userNo, req);

        // Then
        verify(userRepository).findById(userNo);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);

        assertThat(updated).isSameAs(user);
        assertThat(updated.getNickname()).isEqualTo("newNick");
    }

    @Test
    void updateUserWhenNicknameUserNotFound() {
        // Given
        ChangeNicknameRequest req = new ChangeNicknameRequest("newNick");
        when(userRepository.findById(userNo)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userService.updateNickname(userNo, req))
                .isInstanceOf(RestApiException.class)
                .extracting("errorCode.code")
                .isEqualTo(GlobalErrorStatus._NOT_FOUND.getCode().getCode());

        verify(userRepository).findById(userNo);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    // --- updatePassword ---

    @Test
    void updateUserWhenPasswordValid() {
        // Given
        User user = User.builder().password("oldEnc").build();
        ChangePasswordRequest req = new ChangePasswordRequest("rawPwd!", "rawPwd!");
        when(userRepository.findById(userNo)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("rawPwd!")).thenReturn("ENC(rawPwd!)");

        // When
        userService.updatePassword(userNo, req);

        // Then
        verify(userRepository).findById(userNo);
        verify(passwordEncoder).encode("rawPwd!");

        // 저장 호출 없음 (현재 구현상 save 하지 않음)
        verifyNoMoreInteractions(userRepository, passwordEncoder);

        // 엔티티의 상태가 변경되었는지 확인
        assertThat(user.getPassword()).isEqualTo("ENC(rawPwd!)");
    }

    @Test
    void updateUserWhenPasswordUserNotFound() {
        // Given
        ChangePasswordRequest req = new ChangePasswordRequest("rawPwd!", "rawPwd!");
        when(userRepository.findById(userNo)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userService.updatePassword(userNo, req))
                .isInstanceOf(RestApiException.class)
                .extracting("errorCode.code")
                .isEqualTo(GlobalErrorStatus._NOT_FOUND.getCode().getCode());

        verify(userRepository).findById(userNo);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updateUserWhenPasswordMismatch() {
        // Given
        ChangePasswordRequest req = new ChangePasswordRequest("rawPwd!", "DIFF!");

        // When / Then
        assertThatThrownBy(() -> userService.updatePassword(userNo, req))
                .isInstanceOf(RestApiException.class)
                .extracting("errorCode.code")
                .isEqualTo(GlobalErrorStatus.PASSWORD_NOT_MATCH.getCode().getCode());

        // findById/encode 호출 없어야 함 (가드에서 끝)
        verifyNoInteractions(userRepository, passwordEncoder);
    }
}