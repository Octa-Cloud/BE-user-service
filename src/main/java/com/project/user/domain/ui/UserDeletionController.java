package com.project.user.domain.ui;

import com.project.user.domain.application.dto.request.UserDeletionRequest;
import com.project.user.domain.application.usecase.UserDeletionUseCase;
import com.project.user.domain.ui.spec.UserDeletionApiSpec;
import com.project.user.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserDeletionController implements UserDeletionApiSpec {

    private final UserDeletionUseCase userDeletionUseCase;

    @Override
    public BaseResponse<String> deleteUser(UserDeletionRequest request) {
        userDeletionUseCase.execute(request);
        return BaseResponse.onSuccess("User deleted successfully.");
    }
}