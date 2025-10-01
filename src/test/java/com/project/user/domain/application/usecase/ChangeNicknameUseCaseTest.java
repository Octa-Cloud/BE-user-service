package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.request.ChangeNicknameRequest;
import com.project.user.domain.application.dto.response.ChangeNicknameResponse;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.service.UserService;
import com.project.user.global.exception.RestApiException;
import com.project.user.global.exception.code.status.GlobalErrorStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeNicknameUseCaseTest {

    @Mock private UserService userService;
    @InjectMocks private ChangeNicknameUseCase useCase;

    @Test
    void changeUserWhenNicknameValid() {
        // Given
        Long userNo = 10L;
        ChangeNicknameRequest req = new ChangeNicknameRequest("newNick");
        User updated = User.builder().nickname("newNick").build();
        when(userService.updateNickname(userNo, req)).thenReturn(updated);

        // When
        ChangeNicknameResponse res = useCase.changeNickname(userNo, req);

        // Then
        assertThat(res).isNotNull(); // DTO 상세 구조는 여기서 검증하지 않음(원칙)
        verify(userService).updateNickname(userNo, req);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void changeUserWhenNicknameServiceThrows() {
        // Given
        Long userId = 10L;
        ChangeNicknameRequest req = new ChangeNicknameRequest("newNick");
        when(userService.updateNickname(userId, req))
                .thenThrow(new RestApiException(GlobalErrorStatus._NOT_FOUND));

        // When / Then
        assertThatThrownBy(() -> useCase.changeNickname(userId, req))
                .isInstanceOf(RestApiException.class);
        verify(userService).updateNickname(userId, req);
        verifyNoMoreInteractions(userService);
    }
}