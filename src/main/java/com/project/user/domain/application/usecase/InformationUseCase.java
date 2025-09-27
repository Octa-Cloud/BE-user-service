package com.project.user.domain.application.usecase;


import com.project.user.domain.application.dto.response.InformationResponse;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InformationUseCase {
    private final UserService userService;

    public InformationResponse execute(Long userNo){
        User user = userService.findById(userNo);
        return new InformationResponse(
                user.getName(),
                user.getEmail(),
                user.getNickname(),
                user.getBirth(),
                user.getGender());
    }
}
