package com.payriff.sdk.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class CompleteRequest {

    private final String orderId;
    private final BigDecimal amount;
    private final String callbackUrl;

    private CompleteRequest(Builder b) {
        this.orderId = Objects.requireNonNull(b.orderId, "orderId");
        this.amount = b.amount;
        this.callbackUrl = b.callbackUrl;
    }

    public static Builder builder(String orderId) {
        return new Builder(orderId);
    }

    public static final class Builder {
        private final String orderId;
        private BigDecimal amount;
        private String callbackUrl;

        private Builder(String orderId) {
            this.orderId = orderId;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder callbackUrl(String callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        public CompleteRequest build() {
            return new CompleteRequest(this);
        }
    }
}