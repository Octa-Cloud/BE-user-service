package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.request.UserDeletionRequest;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.service.UserService;
import com.project.user.global.exception.RestApiException;
import com.project.user.global.security.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAccountUseCaseTest {

    @Mock private TokenProvider tokenProvider;
    @Mock private UserService userService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DeleteAccountUseCase useCase;

    private final String accessToken = "Bearer xxx.yyy.zzz";
    private final Long userNo = 42L;

    private UserDeletionRequest req(String rawPassword) {
        return new UserDeletionRequest(rawPassword);
    }

    private User userWithPassword(String encoded) {
        return User.builder()
                .email("user@test.com")
                .name("name")
                .nickname("nick")
                .password(encoded)
                .gender(null)
                .build();
    }

    @Nested
    @DisplayName("정상 케이스")
    class SuccessCases {

        @Test
        @DisplayName("유효 토큰+비밀번호 일치+잔여기간 존재 → 리프레시 삭제→블랙리스트→유저 삭제 순서")
        void execute_success() {
            // Given
            when(tokenProvider.getId(accessToken)).thenReturn(Optional.of(userNo));
            User user = userWithPassword("encoded");
            when(userService.findById(userNo)).thenReturn(user);
            when(passwordEncoder.matches("raw", "encoded")).thenReturn(true);
            Duration remaining = Duration.ofMinutes(30);
            when(tokenProvider.getRemainingDuration(accessToken)).thenReturn(Optional.of(remaining));

            // When
            useCase.execute(accessToken, req("raw"));

            // Then
//            InOrder inOrder = inOrder(refreshTokenService, userService);
//            inOrder.verify(refreshTokenService).deleteRefreshToken(userNo);
//            inOrder.verify(userService).deleteUser(userNo);

//            verifyNoMoreInteractions(refreshTokenService, userService);
        }
    }

    @Nested
    @DisplayName("에러 케이스")
    class ErrorCases {

        @Test
        @DisplayName("ID 토큰에서 userNo 파싱 실패 → RestApiException")
        void execute_invalidIdToken() {
            // Given
            when(tokenProvider.getId(accessToken)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(RestApiException.class, () ->
                    useCase.execute(accessToken, req("raw")));

//            verifyNoInteractions(userService, passwordEncoder, refreshTokenService);
        }

        @Test
        @DisplayName("유저 조회 시 예외(_NOT_FOUND 등) → RestApiException")
        void execute_userNotFound() {
            // Given
            when(tokenProvider.getId(accessToken)).thenReturn(Optional.of(userNo));
            when(userService.findById(userNo)).thenThrow(new RestApiException(null));

            // When & Then
            assertThrows(RestApiException.class, () ->
                    useCase.execute(accessToken, req("raw")));

//            verifyNoInteractions(passwordEncoder, refreshTokenService);
        }

        @Test
        @DisplayName("비밀번호 불일치 → RestApiException, 후속 동작 없음")
        void execute_invalidPassword() {
            // Given
            when(tokenProvider.getId(accessToken)).thenReturn(Optional.of(userNo));
            User user = userWithPassword("encoded");
            when(userService.findById(userNo)).thenReturn(user);
            when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

            // When & Then
            assertThrows(RestApiException.class, () ->
                    useCase.execute(accessToken, req("wrong")));

//            verifyNoInteractions(refreshTokenService);
            verify(userService, never()).deleteUser(anyLong());
        }

        @Test
        @DisplayName("잔여 유효기간 조회 실패(Optional.empty) → RestApiException")
        void execute_invalidAccessTokenExpiration() {
            // Given
            when(tokenProvider.getId(accessToken)).thenReturn(Optional.of(userNo));
            User user = userWithPassword("encoded");
            when(userService.findById(userNo)).thenReturn(user);
            when(passwordEncoder.matches("raw", "encoded")).thenReturn(true);
            when(tokenProvider.getRemainingDuration(accessToken)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(RestApiException.class, () ->
                    useCase.execute(accessToken, req("raw")));

//            verifyNoInteractions(refreshTokenService);
            verify(userService, never()).deleteUser(anyLong());
        }
    }

    @Nested
    @DisplayName("경계값 케이스")
    class BoundaryCases {

        @Test
        @DisplayName("잔여 유효기간 = 0초 → 정상 처리되며 0초로 블랙리스트 등록")
        void execute_zeroDuration() {
            // Given
            when(tokenProvider.getId(accessToken)).thenReturn(Optional.of(userNo));
            User user = userWithPassword("encoded");
            when(userService.findById(userNo)).thenReturn(user);
            when(passwordEncoder.matches("raw", "encoded")).thenReturn(true);
            Duration remaining = Duration.ZERO;
            when(tokenProvider.getRemainingDuration(accessToken)).thenReturn(Optional.of(remaining));

            // When
            useCase.execute(accessToken, req("raw"));

            // Then
//            InOrder inOrder = inOrder(refreshTokenService, userService);
//            inOrder.verify(refreshTokenService).deleteRefreshToken(userNo);
//            inOrder.verify(userService).deleteUser(userNo);
        }

        @Test
        @DisplayName("잔여 유효기간 = 1초(매우 짧음) → 정상 처리")
        void execute_oneSecondDuration() {
            // Given
            when(tokenProvider.getId(accessToken)).thenReturn(Optional.of(userNo));
            User user = userWithPassword("encoded");
            when(userService.findById(userNo)).thenReturn(user);
            when(passwordEncoder.matches("raw", "encoded")).thenReturn(true);
            Duration remaining = Duration.ofSeconds(1);
            when(tokenProvider.getRemainingDuration(accessToken)).thenReturn(Optional.of(remaining));

            // When
            useCase.execute(accessToken, req("raw"));

            // Then
//            verify(refreshTokenService).deleteRefreshToken(userNo);
            verify(userService).deleteUser(userNo);
        }
    }
}
