package com.project.user.domain.ui;

import com.project.user.domain.application.dto.response.InformationResponse;
import com.project.user.domain.application.usecase.InformationUseCase;
import com.project.user.domain.ui.spec.InformationApiSpec;
import com.project.user.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InformationController implements InformationApiSpec {
    private final InformationUseCase informationUseCase;
    @Override
    public BaseResponse<InformationResponse> information(
            Long userNo
    ){
        return BaseResponse.onSuccess(informationUseCase.execute(userNo));
    }
}
