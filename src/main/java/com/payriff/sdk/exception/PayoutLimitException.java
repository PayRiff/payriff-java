package com.payriff.sdk.exception;

public class PayoutLimitException extends ApiException {

    private static final long serialVersionUID = 1L;

    public PayoutLimitException(String message, int httpStatus, String code, String responseId) {
        super(message, httpStatus, code, responseId);
    }
}