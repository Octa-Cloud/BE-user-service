package com.project.user.domain.ui;

import com.project.user.domain.application.dto.response.SummaryResponse;
import com.project.user.domain.application.usecase.SummaryUseCase;
import com.project.user.domain.ui.spec.SummaryApiSepc;
import com.project.user.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SummaryController implements SummaryApiSepc {
    private final SummaryUseCase summaryUseCase;
    @Override
    public BaseResponse<SummaryResponse> summary(
            Long userNo
    ){
        return BaseResponse.onSuccess(summaryUseCase.execute(userNo));
    }
}
