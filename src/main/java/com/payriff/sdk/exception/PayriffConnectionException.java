package com.payriff.sdk.exception;

public class PayriffConnectionException extends PayriffException {

    private static final long serialVersionUID = 1L;

    public PayriffConnectionException(String message, Throwable cause) {
        super(message, 0, null, null, cause);
    }
}