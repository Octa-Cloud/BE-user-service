package com.project.user.domain.domain.service;

import com.project.user.domain.application.dto.request.SleepGoalRequest;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.repository.UserRepository;
import com.project.user.global.exception.RestApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SleepGoalService 테스트")
class SleepGoalServiceTest {

    @Mock
    private SleepGoalRepository sleepGoalRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SleepGoalService sleepGoalService;

    private Long userNo;
    private User user;
    private SleepGoalRequest request;

    @BeforeEach // 모든 테스트코드 전 반복 작업
    void setUp() {
        userNo = 1L;
        user = User.builder().userNo(userNo).build();
        request = new SleepGoalRequest(LocalTime.of(23, 0), LocalTime.of(7, 0), 480);
    }

    // ================= 1. getSleepGoal ===============================
    @Nested
    @DisplayName("getSleepGoal 메서드: ")
    class GetSleepGoalTest {

        // DB에서 userNo에 해당하는 SleepGoal이 존재 -> 수면 목표 조회 성공
        @Test
        @DisplayName("수면 목표가 존재하면 조회에 성공한다")
        void getSleepGoal_Success() {
            // Given
            SleepGoal expectedGoal = createSleepGoal(userNo, request.goalBedTime(), request.goalWakeTime(), request.goalTotalSleepTime());
            when(sleepGoalRepository.findByUserNo(userNo)).thenReturn(Optional.of(expectedGoal));

            // When
            SleepGoal result = sleepGoalService.getSleepGoal(userNo);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUserNo()).isEqualTo(userNo);
            verify(sleepGoalRepository, times(1)).findByUserNo(userNo);
        }
    }

    // ================= 2. setSleepGoal ===============================
    @Nested
    @DisplayName("setSleepGoal 메서드: ")
    class SetSleepGoalTest {

        // 이미 DB에 수면 목표 존재 -> 기존 목표 업데이트 성공
        @Test
        @DisplayName("기존 수면 목표가 있으면 업데이트에 성공한다")
        void setSleepGoal_UpdateExisting_Success() {
            // Given
            SleepGoal existingGoal = createSleepGoal(userNo, LocalTime.of(22, 0), LocalTime.of(6, 0), 480);
            when(sleepGoalRepository.findByUserNo(userNo)).thenReturn(Optional.of(existingGoal));
            when(sleepGoalRepository.save(any(SleepGoal.class))).thenReturn(existingGoal);

            // When
            SleepGoal result = sleepGoalService.setSleepGoal(userNo, request);

            // Then
            assertThat(result.getGoalBedTime()).isEqualTo(request.goalBedTime());
            verify(sleepGoalRepository, times(1)).findByUserNo(userNo);
            verify(sleepGoalRepository, times(1)).save(existingGoal);
            verify(userRepository, never()).findById(any());
        }

        // 기존 목표 없음 + 유저는 존재함 -> 새 목표 생성 성공
        @Test
        @DisplayName("수면 목표가 없으면 새로 생성한다")
        void setSleepGoal_CreateNew_Success() {
            // Given
            SleepGoal newGoal = createSleepGoal(userNo, request.goalBedTime(), request.goalWakeTime(), request.goalTotalSleepTime());
            when(sleepGoalRepository.findByUserNo(userNo)).thenReturn(Optional.empty());
            when(userRepository.findById(userNo)).thenReturn(Optional.of(user));
            when(sleepGoalRepository.save(any(SleepGoal.class))).thenReturn(newGoal);

            // When
            SleepGoal result = sleepGoalService.setSleepGoal(userNo, request);

            // Then
            assertThat(result.getUserNo()).isEqualTo(userNo);
            verify(sleepGoalRepository, times(1)).findByUserNo(userNo);
            verify(userRepository, times(1)).findById(userNo);
            verify(sleepGoalRepository, times(1)).save(any(SleepGoal.class));
        }

        // 기존 목표 없음 + 유저도 없음 -> 유저가 존재하지 않음
        @Test
        @DisplayName("수면 목표가 없고 사용자도 존재하지 않으면 USER_NOT_FOUND 예외가 발생한다")
        void setSleepGoal_UserNotFound_ThrowsException() {
            // Given
            when(sleepGoalRepository.findByUserNo(userNo)).thenReturn(Optional.empty());
            when(userRepository.findById(userNo)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> sleepGoalService.setSleepGoal(userNo, request))
                    .isInstanceOf(RestApiException.class)
                    .hasMessageContaining("해당 유저를 찾을 수 없습니다.");
            verify(sleepGoalRepository, never()).save(any());
        }
    }

    private SleepGoal createSleepGoal(Long userNo, LocalTime bedTime, LocalTime wakeTime, Integer totalSleepTime) {
        return SleepGoal.builder()
                .goalNo(1L).userNo(userNo).goalBedTime(bedTime)
                .goalWakeTime(wakeTime).goalTotalSleepTime(totalSleepTime).build();
    }
}