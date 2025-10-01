package com.project.user;

import com.project.user.domain.application.dto.response.SummaryResponse;
import com.project.user.domain.application.usecase.SummaryUseCase;
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

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.when;

public class SummaryUseCaseTest
{
    @Mock
    UserService userService;

    @InjectMocks
    SummaryUseCase summaryUseCase;

    private User user;

    @BeforeEach
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        user = User.builder()
                .userNo(100L)
                .email("test@email.com")
                .password("testpass")
                .avgScore(80)
                .avgSleepTime(7)
                .avgBedTime(LocalTime.of(23,10))
                .build();
    }

    @Test
    @DisplayName("유저가 유효 할때 해당 평균 수치를 반환한다.")
    void getSummaryWhenUserExists(){
        Long userNo = 100L;
        when(userService.findById(userNo)).thenReturn(user);

        // when
        SummaryResponse response = summaryUseCase.execute(userNo);

        // then
        assertNotNull(response);
        assertEquals(user.getAvgScore(), response.score());
        assertEquals(user.getAvgSleepTime(), response.sleepTime());
        assertEquals(user.getAvgBedTime(), response.bedTime());
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
                () -> summaryUseCase.execute(userNo));

        BaseCode expected = GlobalErrorStatus._NOT_FOUND.getCode();

        assertEquals(expected.getHttpStatus(), exception.getErrorCode().getHttpStatus());
        assertEquals(expected.isSuccess(), exception.getErrorCode().isSuccess());
        assertEquals(expected.getCode(), exception.getErrorCode().getCode());
        assertEquals(expected.getMessage(), exception.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("avgScore, avgSleepTime, avgBedTime 기본값이 들어가는지 테스트")
    void prePersistDefaultsTest() {
        User user = User.builder()
                .email("nulltest@email.com")
                .password("pass")
                .build();

        user.prePersist(); // 수동 호출
        assertEquals(0, user.getAvgScore());
        assertEquals(0, user.getAvgSleepTime());
        assertEquals(LocalTime.of(23, 0), user.getAvgBedTime());
    }
}
