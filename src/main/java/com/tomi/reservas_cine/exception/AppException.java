package com.tomi.reservas_cine.exception;

public class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMensaje());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() { return errorCode; }
}