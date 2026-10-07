package com.payriff.sdk.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum PaymentStatus {
    CREATED, APPROVED, CANCELED, DECLINED, REFUNDED, PREAUTH_APPROVED, EXPIRED, REVERSE, PARTIAL_REFUND, PARTIAL,
    ACCEPTED, REFUND_IN_PROGRESS, CASH, PENDING, PREAUTH_EXPIRED, IN_REVIEW,
    @JsonEnumDefaultValue
    UNKNOWN
}
