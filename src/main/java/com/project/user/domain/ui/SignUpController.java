package com.project.user.domain.ui;

import com.project.user.domain.application.dto.request.SignUpRequest;
import com.project.user.domain.application.usecase.SignUpUseCase;
import com.project.user.domain.ui.spec.SignUpApiSpec;
import com.project.user.global.common.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SignUpController implements SignUpApiSpec {

    private final SignUpUseCase signUpUseCase;

    @Override
    public BaseResponse<Void> signUp(
            @RequestBody @Valid SignUpRequest request
    ) {
        signUpUseCase.execute(request);
        return BaseResponse.onSuccess();
    }
}
