package com.payriff.sdk.exception;

public class RequestRejectedException extends ApiException {

    private static final long serialVersionUID = 1L;

    public RequestRejectedException(String message, int httpStatus, String code, String responseId) {
        super(message, httpStatus, code, responseId);
    }
}