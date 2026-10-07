package com.payriff.sdk.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum InvoiceStatus {
    PENDING, ERROR, EXPIRED, PARTIAL, COMPLETE, CASH, DECLINED, CANCELED,
    @JsonEnumDefaultValue
    UNKNOWN
}
