package com.payriff.sdk.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class RefundRequest {

    private final String orderId;
    private final BigDecimal amount;
    private final String refundReason;
    private final String callbackUrl;

    private RefundRequest(Builder b) {
        this.orderId = Objects.requireNonNull(b.orderId, "orderId");
        this.amount = b.amount;
        this.refundReason = b.refundReason;
        this.callbackUrl = b.callbackUrl;
    }

    public static Builder builder(String orderId) {
        return new Builder(orderId);
    }

    public static final class Builder {
        private final String orderId;
        private BigDecimal amount;
        private String refundReason;
        private String callbackUrl;

        private Builder(String orderId) {
            this.orderId = orderId;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder refundReason(String refundReason) {
            this.refundReason = refundReason;
            return this;
        }

        public Builder callbackUrl(String callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        public RefundRequest build() {
            return new RefundRequest(this);
        }
    }
}