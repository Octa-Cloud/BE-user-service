package com.project.user;

import com.project.user.domain.application.dto.response.GetProfileResponse;
import com.project.user.domain.application.usecase.GetProfileUseCase;
import com.project.user.domain.domain.entity.Gender;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.service.UserService;
import com.project.user.global.exception.RestApiException;
import com.project.user.global.exception.code.BaseCode;
import com.project.user.global.exception.code.status.GlobalErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.when;

public class GetProfileUseCaseTest
{
    @Mock
    UserService userService;

    @InjectMocks
    GetProfileUseCase getProfileUseCase;

    private User user;

    @BeforeEach
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        user = User.builder()
                .userNo(100L)
                .name("test")
                .nickname("nicktest")
                .email("test@email.com")
                .birth(LocalDate.now())
                .password("testpass")
                .gender(Gender.MALE)
                .build();
    }

    @Test
    @DisplayName("유저가 유효 할때 해당 유저 정보를 반환한다.")
    void getProfileWhenUserExists(){
        Long userNo = 100L;
        when(userService.findById(userNo)).thenReturn(user);

        // when
        GetProfileResponse response = getProfileUseCase.execute(userNo);

        // then
        assertNotNull(response);
        assertEquals(user.getName(), response.name());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getNickname(), response.nickname());
        assertEquals(user.getBirth(), response.birth());
        assertEquals(user.getGender(), response.gender());
    }

    @Test
    @DisplayName("유저가 유효하지 않을 때 예외가 발생한다.")
    void getProfileWhenUserNotExists(){
        // given
        Long userNo = 999L;
        when(userService.findById(userNo))
                .thenThrow(new RestApiException(GlobalErrorStatus._NOT_FOUND));

        // when & then
        RestApiException exception = assertThrows(RestApiException.class,
                () -> getProfileUseCase.execute(userNo));

        BaseCode expected = GlobalErrorStatus._NOT_FOUND.getCode();

// 핵심 필드만 비교
        assertEquals(expected.getHttpStatus(), exception.getErrorCode().getHttpStatus());
        assertEquals(expected.isSuccess(), exception.getErrorCode().isSuccess());
        assertEquals(expected.getCode(), exception.getErrorCode().getCode());
        assertEquals(expected.getMessage(), exception.getErrorCode().getMessage());
    }
}
