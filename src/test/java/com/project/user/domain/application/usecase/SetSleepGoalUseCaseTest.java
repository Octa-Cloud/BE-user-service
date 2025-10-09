package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.request.SleepGoalRequest;
import com.project.user.global.exception.RestApiException;
import com.project.user.global.exception.code.status.GlobalErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SetSleepGoalUseCase 테스트")
class SetSleepGoalUseCaseTest {

    @Mock
    private SleepGoalService sleepGoalService;

    @InjectMocks
    private SetSleepGoalUseCase setSleepGoalUseCase;

    private Long userNo;
    private SleepGoalRequest createRequest;
    private SleepGoal createdGoal;

    @BeforeEach // 모든 테스트코드 전 반복 작업
    void setUp() {
        userNo = 1L;
        createRequest = new SleepGoalRequest(LocalTime.of(23, 0), LocalTime.of(7, 0), 480);
        createdGoal = createSleepGoal(userNo, createRequest.goalBedTime(), createRequest.goalWakeTime(), createRequest.goalTotalSleepTime());
    }

    // ================= 1. 정상 시나리오 ===============================
    @Nested
    @DisplayName("정상 시나리오")
    class NormalScenario {

        // 1. 정상 시나리오(1) : 새로운 수면 목표 생성
        @Test
        @DisplayName("새로운 수면 목표 생성에 성공하면 SleepGoalResponse를 반환한다")
        void setSleepGoal_CreateNew_Success() {
            // Given
            when(sleepGoalService.setSleepGoal(eq(userNo), any(SleepGoalRequest.class))).thenReturn(createdGoal);

            // When
            SleepGoalResponse response = setSleepGoalUseCase.setSleepGoal(userNo, createRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.userNo()).isEqualTo(userNo);
            assertThat(response.goalTotalSleepTime()).isEqualTo(480);
            verify(sleepGoalService, times(1)).setSleepGoal(eq(userNo), any(SleepGoalRequest.class));
        }

        // 1. 정상 시나리오(2) : 기존 수면 목표 업데이트
        @Test
        @DisplayName("기존 수면 목표 업데이트에 성공하면 SleepGoalResponse를 반환한다")
        void setSleepGoal_UpdateExisting_Success() {
            // Given
            SleepGoalRequest updateRequest = new SleepGoalRequest(LocalTime.of(22, 30), LocalTime.of(6, 30), 480);
            SleepGoal updatedGoal = createSleepGoal(userNo, updateRequest.goalBedTime(), updateRequest.goalWakeTime(), updateRequest.goalTotalSleepTime());
            when(sleepGoalService.setSleepGoal(eq(userNo), any(SleepGoalRequest.class))).thenReturn(updatedGoal);

            // When
            SleepGoalResponse response = setSleepGoalUseCase.setSleepGoal(userNo, updateRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.goalBedTime()).isEqualTo(LocalTime.of(22, 30));
            verify(sleepGoalService, times(1)).setSleepGoal(eq(userNo), any(SleepGoalRequest.class));
        }
    }

    // ================= 2. 에러 시나리오 ===============================
    @Nested
    @DisplayName("에러 시나리오")
    class ErrorScenario {

        // 2. 에러 시나리오 - 사용자 존재하지 않는 경우 예외 발생
        @Test
        @DisplayName("사용자가 존재하지 않으면 USER_NOT_FOUND 예외가 발생한다")
        void setSleepGoal_UserNotFound_ThrowsException() {
            // Given
            when(sleepGoalService.setSleepGoal(eq(userNo), any(SleepGoalRequest.class)))
                    .thenThrow(new RestApiException(GlobalErrorStatus.USER_NOT_FOUND));

            // When & Then
            assertThatThrownBy(() -> setSleepGoalUseCase.setSleepGoal(userNo, createRequest))
                    .isInstanceOf(RestApiException.class)
                    .hasMessageContaining("해당 유저를 찾을 수 없습니다.");
            verify(sleepGoalService, times(1)).setSleepGoal(eq(userNo), any(SleepGoalRequest.class));
        }
    }

    private SleepGoal createSleepGoal(Long userNo, LocalTime bedTime, LocalTime wakeTime, Integer totalSleepTime) {
        return SleepGoal.builder()
                .goalNo(1L).userNo(userNo).goalBedTime(bedTime)
                .goalWakeTime(wakeTime).goalTotalSleepTime(totalSleepTime).build();
    }
}