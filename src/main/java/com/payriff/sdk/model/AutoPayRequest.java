package com.payriff.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public final class AutoPayRequest {

    private final String cardUuid;
    private final BigDecimal amount;
    private final Operation operation;
    private final Currency currency;
    private final String description;
    private final String callbackUrl;
    private final Boolean threeDS;
    @JsonProperty("isOneCLickPayment")
    private final Boolean oneClickPayment;
    @JsonIgnore
    private final String requestRrn;

    private AutoPayRequest(Builder b) {
        this.cardUuid = Objects.requireNonNull(b.cardUuid, "cardUuid");
        this.amount = Objects.requireNonNull(b.amount, "amount");
        this.operation = Objects.requireNonNull(b.operation, "operation");
        this.currency = Objects.requireNonNull(b.currency, "currency");
        this.description = Objects.requireNonNull(b.description, "description");
        this.callbackUrl = b.callbackUrl;
        this.threeDS = b.threeDS;
        this.oneClickPayment = b.oneClickPayment;
        this.requestRrn = b.requestRrn;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getRequestRrn() {
        return requestRrn;
    }

    public static final class Builder {
        private String cardUuid;
        private BigDecimal amount;
        private Operation operation = Operation.PURCHASE;
        private Currency currency = Currency.AZN;
        private String description;
        private String callbackUrl;
        private Boolean threeDS;
        private Boolean oneClickPayment;
        private String requestRrn;

        private Builder() {
        }

        public Builder cardUuid(String cardUuid) {
            this.cardUuid = cardUuid;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder operation(Operation operation) {
            this.operation = operation;
            return this;
        }

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder callbackUrl(String callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        public Builder threeDS(boolean threeDS) {
            this.threeDS = threeDS;
            return this;
        }

        public Builder oneClickPayment(boolean oneClickPayment) {
            this.oneClickPayment = oneClickPayment;
            return this;
        }

        public Builder requestRrn(String requestRrn) {
            this.requestRrn = requestRrn;
            return this;
        }

        public AutoPayRequest build() {
            return new AutoPayRequest(this);
        }
    }
}