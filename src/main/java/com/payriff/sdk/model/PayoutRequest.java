package com.payriff.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Objects;

public final class PayoutRequest {

    private final BigDecimal transferAmount;
    private final String description;
    private final String fullName;
    private final String finCode;
    private final String cardPan;
    private final String bankName;
    private final String cardType;
    private final String requestRrn;
    private final String customerCode;
    private final String voen;
    private final String birthDate;
    private final String callbackUrl;
    @JsonIgnore
    private final String idempotencyKey;

    private PayoutRequest(Builder b) {
        this.transferAmount = Objects.requireNonNull(b.transferAmount, "transferAmount");
        if (transferAmount.compareTo(BigDecimal.ONE) < 0) {
            throw new IllegalArgumentException("transferAmount must be at least 1");
        }
        this.description = Objects.requireNonNull(b.description, "description");
        this.fullName = Objects.requireNonNull(b.fullName, "fullName");
        this.finCode = Objects.requireNonNull(b.finCode, "finCode");
        this.cardPan = b.cardPan;
        this.bankName = b.bankName;
        this.cardType = b.cardType;
        this.requestRrn = b.requestRrn;
        this.customerCode = b.customerCode;
        this.voen = b.voen;
        this.birthDate = b.birthDate;
        this.callbackUrl = b.callbackUrl;
        this.idempotencyKey = b.idempotencyKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    @Override
    public String toString() {
        String masked = cardPan == null || cardPan.length() < 10
                ? null : cardPan.substring(0, 6) + "******" + cardPan.substring(cardPan.length() - 4);
        return "PayoutRequest{transferAmount=" + transferAmount + ", requestRrn=" + requestRrn + ", cardPan=" + masked + "}";
    }

    public static final class Builder {
        private BigDecimal transferAmount;
        private String description;
        private String fullName;
        private String finCode;
        private String cardPan;
        private String bankName;
        private String cardType;
        private String requestRrn;
        private String customerCode;
        private String voen;
        private String birthDate;
        private String callbackUrl;
        private String idempotencyKey;

        private Builder() {
        }

        public Builder transferAmount(BigDecimal transferAmount) {
            this.transferAmount = transferAmount;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder finCode(String finCode) {
            this.finCode = finCode;
            return this;
        }

        public Builder cardPan(String cardPan) {
            this.cardPan = cardPan;
            return this;
        }

        public Builder bankName(String bankName) {
            this.bankName = bankName;
            return this;
        }

        public Builder cardType(String cardType) {
            this.cardType = cardType;
            return this;
        }

        public Builder requestRrn(String requestRrn) {
            this.requestRrn = requestRrn;
            return this;
        }

        public Builder customerCode(String customerCode) {
            this.customerCode = customerCode;
            return this;
        }

        public Builder voen(String voen) {
            this.voen = voen;
            return this;
        }

        public Builder birthDate(String birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder callbackUrl(String callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public PayoutRequest build() {
            return new PayoutRequest(this);
        }
    }
}