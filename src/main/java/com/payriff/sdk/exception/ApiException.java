package com.payriff.sdk.exception;


public class ApiException extends PayriffException {

    private static final long serialVersionUID = 1L;

    public ApiException(String message, int httpStatus, String code, String responseId) {
        super(message, httpStatus, code, responseId, null);
    }
}