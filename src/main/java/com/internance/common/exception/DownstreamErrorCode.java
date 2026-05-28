package com.internance.common.exception;

import org.springframework.http.HttpStatus;

public record DownstreamErrorCode(String code, String message, HttpStatus status) implements ErrorCode {

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
