package com.project.user.domain.ui;

import com.project.user.domain.application.dto.request.ChangeNicknameRequest;
import com.project.user.domain.application.dto.response.ChangeNicknameResponse;
import com.project.user.domain.application.usecase.ChangeNicknameUseCase;
import com.project.user.domain.ui.spec.ChangeNicknameApiSpec;
import com.project.user.global.annotation.CurrentUser;
import com.project.user.global.common.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChangeNicknameController implements ChangeNicknameApiSpec {

    private final ChangeNicknameUseCase changeNicknameUseCase;

    @Override
    public BaseResponse<ChangeNicknameResponse> changeNickname(
            @CurrentUser Long userNo,
            @Valid @RequestBody ChangeNicknameRequest request
            ){
        return BaseResponse.onSuccess(changeNicknameUseCase.changeNickname(userNo,request));
    }
}
