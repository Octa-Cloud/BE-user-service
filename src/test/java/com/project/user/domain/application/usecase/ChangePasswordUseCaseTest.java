package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.request.ChangePasswordRequest;
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
class ChangePasswordUseCaseTest {

    @Mock private UserService userService;
    @InjectMocks private ChangePasswordUseCase useCase;

    @Test
    void changeUserWhenPasswordValid() {
        // Given
        Long userNo = 11L;
        ChangePasswordRequest req = new ChangePasswordRequest("rawPwd!", "rawPwd!");

        // When
        useCase.changePassword(userNo, req);

        // Then
        verify(userService).updatePassword(userNo, req);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void changeUserWhenPasswordServiceThrows() {
        // Given
        Long userId = 11L;
        ChangePasswordRequest req = new ChangePasswordRequest("rawPwd!", "rawPwd!");
        doThrow(new RestApiException(GlobalErrorStatus._NOT_FOUND))
                .when(userService).updatePassword(userId, req);

        // When / Then
        assertThatThrownBy(() -> useCase.changePassword(userId, req))
                .isInstanceOf(RestApiException.class);
        verify(userService).updatePassword(userId, req);
        verifyNoMoreInteractions(userService);
    }
}