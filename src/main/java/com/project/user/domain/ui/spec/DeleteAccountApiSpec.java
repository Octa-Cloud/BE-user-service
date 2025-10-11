package com.project.user.domain.ui.spec;

import com.project.user.domain.application.dto.request.UserDeletionRequest;
import com.project.user.global.annotation.AccessToken;
import com.project.user.global.annotation.CurrentUser;
import com.project.user.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User Deletion", description = "회원 탈퇴 API")
public interface DeleteAccountApiSpec {

    @Operation(summary = "회원 탈퇴", description = "비밀번호를 확인하여 회원을 탈퇴시키는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "회원 정보 없음"),
            @ApiResponse(responseCode = "400", description = "비밀번호 불일치 또는 잘못된 요청")
    })
    @DeleteMapping("/api/users")
    BaseResponse<Void> deleteUser(
            @CurrentUser Long userNo,
            @AccessToken String accessToken,
            @RequestBody @Valid UserDeletionRequest request
    );
}