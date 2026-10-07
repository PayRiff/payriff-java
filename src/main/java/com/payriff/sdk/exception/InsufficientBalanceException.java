package com.payriff.sdk.exception;

public class InsufficientBalanceException extends ApiException {

    private static final long serialVersionUID = 1L;

    public InsufficientBalanceException(String message, int httpStatus, String code, String responseId) {
        super(message, httpStatus, code, responseId);
    }
}