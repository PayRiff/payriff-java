package com.payriff.sdk.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum CardSaveStatus {
    CREATED, VERIFIED, REVERSED, REVERSE_FAILED, DECLINED, EXPIRED,
    @JsonEnumDefaultValue
    UNKNOWN
}
