package com.project.user.domain.application.dto.response;

import com.project.user.domain.domain.entity.User;
import lombok.Builder;

@Builder
public record ChangeNicknameResponse(
    String nickName
) {

    public static ChangeNicknameResponse from(User user) {
        return ChangeNicknameResponse.builder()
                .nickName(user.getNickname())
                .build();
    }
}
