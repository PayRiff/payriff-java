package com.payriff.sdk.exception;

public class AuthenticationException extends ApiException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message, int httpStatus, String code, String responseId) {
        super(message, httpStatus, code, responseId);
    }
}