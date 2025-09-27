package com.project.user.domain.application.usecase;


import com.project.user.domain.application.dto.response.GetProfileResponse;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetProfileUseCase {
    private final UserService userService;

    public GetProfileResponse execute(Long userNo){
        User user = userService.findById(userNo);
        return new GetProfileResponse(
                user.getName(),
                user.getEmail(),
                user.getNickname(),
                user.getBirth(),
                user.getGender());
    }
}
