package com.project.user.domain.application.usecase;

import com.project.user.domain.application.dto.request.UserDeletionRequest;
import com.project.user.domain.domain.service.TokenBlacklistService;
import com.project.user.domain.domain.service.UserDeletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDeletionUseCase {
    private final UserDeletionService userDeletionService;
    private final TokenBlacklistService tokenBlacklistService;

    @Transactional
    public void execute(UserDeletionRequest request) {
        userDeletionService.delete(request.userId(), request.password());
        // 추가: 회원탈퇴 시 모든 토큰을 무효화하는 로직을 여기에 추가할 수 있습니다.
        // 예를 들어, 해당 유저의 모든 리프레시 토큰을 블랙리스트 처리
        // tokenBlacklistService.blacklistAllTokensForUser(request.userId());
    }
}