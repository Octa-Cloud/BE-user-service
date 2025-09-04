package com.project.user.domain.ui;

import com.project.user.domain.application.dto.response.TokenReissueResponse;
import com.project.user.domain.application.usecase.TokenReissueUseCase;
import com.project.user.domain.ui.spec.TokenReissueApiSpec;
import com.project.user.global.annotation.CurrentUser;
import com.project.user.global.annotation.RefreshToken;
import com.project.user.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenReissueController implements TokenReissueApiSpec {

    private final TokenReissueUseCase tokenReissueUseCase;

    @Override
    public BaseResponse<TokenReissueResponse> reissue(
            @CurrentUser Long userNo,
            @RefreshToken String refreshToken
    ) {
        return BaseResponse.onSuccess(tokenReissueUseCase.execute(refreshToken, userNo));
    }
}
