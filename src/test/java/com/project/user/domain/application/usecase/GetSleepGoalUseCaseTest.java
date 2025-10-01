package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.response.SleepGoalResponse;
import com.project.user.domain.domain.entity.SleepGoal;
import com.project.user.domain.domain.service.SleepGoalService;
import com.project.user.global.exception.RestApiException;
import com.project.user.global.exception.code.status.GlobalErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetSleepGoalUseCase 테스트")
class GetSleepGoalUseCaseTest {

    @Mock
    private SleepGoalService sleepGoalService;

    @InjectMocks
    private GetSleepGoalUseCase getSleepGoalUseCase;

    private Long userNo;
    private SleepGoal mockSleepGoal;

    @BeforeEach // 모든 테스트코드 전 반복 작업
    void setUp() {
        userNo = 1L;
        mockSleepGoal = createSleepGoal(
                userNo,
                LocalTime.of(23, 0),
                LocalTime.of(7, 0),
                480
        );
    }

    // ================= 1. 정상 시나리오 ===============================
    @Nested
    @DisplayName("정상 시나리오")
    class NormalScenario {

        // 1. 정상 시나리오 - 수면 목표 조회 성공
        @Test
        @DisplayName("수면 목표 조회에 성공하면 SleepGoalResponse를 반환한다")
        void getSleepGoal_Success() {
            // Given
            when(sleepGoalService.getSleepGoal(userNo)).thenReturn(mockSleepGoal);

            // When
            SleepGoalResponse response = getSleepGoalUseCase.getSleepGoal(userNo);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.userNo()).isEqualTo(userNo);
            assertThat(response.goalBedTime()).isEqualTo(mockSleepGoal.getGoalBedTime());
            verify(sleepGoalService, times(1)).getSleepGoal(userNo);
        }
    }

    // ================= 2. 에러 시나리오 ===============================v
    @Nested
    @DisplayName("에러 시나리오")
    class ErrorScenario {

        // 2. 에러 시나리오 - 수면 목표 존재하지 않는 경우 예외 발생
        @Test
        @DisplayName("수면 목표가 존재하지 않으면 SLEEP_GOAL_NOT_FOUND 예외가 발생한다")
        void getSleepGoal_NotFound_ThrowsException() {
            // Given
            when(sleepGoalService.getSleepGoal(userNo))
                    .thenThrow(new RestApiException(GlobalErrorStatus.SLEEP_GOAL_NOT_FOUND));

            // When & Then
            assertThatThrownBy(() -> getSleepGoalUseCase.getSleepGoal(userNo))
                    .isInstanceOf(RestApiException.class)
                    .hasMessageContaining("해당 유저의 수면 목표가 존재하지 않습니다.");
            verify(sleepGoalService, times(1)).getSleepGoal(userNo);
        }
    }

    // ================= 3. 경계값 시나리오 ===============================
    @Nested
    @DisplayName("경계값 시나리오")
    class BoundaryScenario {

        // 3. 경계값 시나리오 - 총 수면 시간이 0분 또는 1440분인 경우 (극단적인 수면 시간)
        @ParameterizedTest
        @ValueSource(ints = {0, 1440}) // 0과 1440을 순서대로 테스트
        @DisplayName("총 수면 시간이 0분 또는 1440분인 목표를 조회할 수 있다")
        void getSleepGoal_BoundarySleepTime(int sleepTime) {
            // Given
            SleepGoal boundaryGoal = createSleepGoal(userNo, LocalTime.of(0, 0), LocalTime.of(0, 0), sleepTime);
            when(sleepGoalService.getSleepGoal(userNo)).thenReturn(boundaryGoal);

            // When
            SleepGoalResponse response = getSleepGoalUseCase.getSleepGoal(userNo);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.goalTotalSleepTime()).isEqualTo(sleepTime);
        }
    }

    // Helper Method
    private SleepGoal createSleepGoal(Long userNo, LocalTime bedTime, LocalTime wakeTime, Integer totalSleepTime) {
        return SleepGoal.builder()
                .goalNo(1L).userNo(userNo).goalBedTime(bedTime)
                .goalWakeTime(wakeTime).goalTotalSleepTime(totalSleepTime).build();
    }
}