package com.project.user.domain.ui;

import com.project.user.domain.application.dto.response.GetProfileResponse;
import com.project.user.domain.application.usecase.GetProfileUseCase;
import com.project.user.domain.ui.spec.GetProfileApiSpec;
import com.project.user.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetProfileController implements GetProfileApiSpec {
    private final GetProfileUseCase informationUseCase;
    @Override
    public BaseResponse<GetProfileResponse> information(
            Long userNo
    ){
        return BaseResponse.onSuccess(informationUseCase.execute(userNo));
    }
}
