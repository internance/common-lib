package com.internance.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {

    BAD_REQUEST("G400", "Bad Request", HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED("G400_1", "Validation Failed", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("G401", "Unauthorized", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("G403", "Forbidden", HttpStatus.FORBIDDEN),
    NOT_FOUND("G404", "Resource Not Found", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED("G405", "Method Not Allowed", HttpStatus.METHOD_NOT_ALLOWED),
    CONFLICT("G409", "Conflict", HttpStatus.CONFLICT),
    INTERNAL_SERVER_ERROR("G500", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
