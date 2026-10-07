package com.payriff.sdk.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum TransferState {
    CREATED, IN_PROGRESS, NOT_FOUND, FAIL, SUCCESS,
    @JsonEnumDefaultValue
    UNKNOWN,
    DAILY_PAYOUT_LIMIT_EXCEEDED
}
