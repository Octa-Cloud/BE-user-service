package com.project.user.domain.ui;

import com.project.user.domain.application.usecase.LogoutUseCase;
import com.project.user.domain.ui.spec.LogoutApiSpec;
import com.project.user.global.annotation.AccessToken;
import com.project.user.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LogoutController implements LogoutApiSpec {

    private final LogoutUseCase logoutUseCase;

    @Override
    public BaseResponse<Void> logout(
            @AccessToken String accessToken
    ) {
        logoutUseCase.execute(accessToken);
        return BaseResponse.onSuccess();
    }
}
