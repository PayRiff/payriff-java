package com.payriff.sdk.exception;

public class ValidationException extends ApiException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message, int httpStatus, String code, String responseId) {
        super(message, httpStatus, code, responseId);
    }
}