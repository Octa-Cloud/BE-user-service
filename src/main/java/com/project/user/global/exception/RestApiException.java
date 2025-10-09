package com.project.user.global.exception;

import com.project.user.global.exception.code.BaseCode;
import com.project.user.global.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RestApiException extends RuntimeException {

    private final BaseCodeInterface errorCode;

    public BaseCode getErrorCode() {
        return this.errorCode.getCode();
    }
}
