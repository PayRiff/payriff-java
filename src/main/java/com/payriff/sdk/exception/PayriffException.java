package com.payriff.sdk.exception;

public class PayriffException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int httpStatus;
    private final String code;
    private final String responseId;

    public PayriffException(String message, int httpStatus, String code, String responseId, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.code = code;
        this.responseId = responseId;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getResponseId() {
        return responseId;
    }
}