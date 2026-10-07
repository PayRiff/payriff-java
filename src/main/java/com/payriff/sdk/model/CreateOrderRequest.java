package com.payriff.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class CreateOrderRequest {

    private final BigDecimal amount;
    private final Currency currency;
    private final Language language;
    private final Operation operation;
    private final String description;
    private final String callbackUrl;
    private final String redirectUrl;
    private final Boolean cardSave;
    private final Boolean threeDS;
    private final AutoPaymentType autoPaymentType;
    private final Installment installment;
    private final String fullName;
    private final String phoneNumber;
    private final Map<String, String> metadata;
    private final Map<String, String> fields;
    @JsonIgnore
    private final String requestRrn;

    private CreateOrderRequest(Builder b) {
        this.amount = Objects.requireNonNull(b.amount, "amount");
        this.currency = Objects.requireNonNull(b.currency, "currency");
        this.language = b.language;
        this.operation = Objects.requireNonNull(b.operation, "operation");
        this.description = b.description;
        this.callbackUrl = b.callbackUrl;
        this.redirectUrl = b.redirectUrl;
        this.cardSave = b.cardSave;
        this.threeDS = b.threeDS;
        this.autoPaymentType = b.autoPaymentType;
        this.installment = b.installment;
        this.fullName = b.fullName;
        this.phoneNumber = b.phoneNumber;
        this.metadata = b.metadata.isEmpty() ? null : new LinkedHashMap<>(b.metadata);
        this.fields = b.fields.isEmpty() ? null : new LinkedHashMap<>(b.fields);
        this.requestRrn = b.requestRrn;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getRequestRrn() {
        return requestRrn;
    }

    public static final class Builder {
        private BigDecimal amount;
        private Currency currency = Currency.AZN;
        private Language language;
        private Operation operation = Operation.PURCHASE;
        private String description;
        private String callbackUrl;
        private String redirectUrl;
        private Boolean cardSave;
        private Boolean threeDS;
        private AutoPaymentType autoPaymentType;
        private Installment installment;
        private String fullName;
        private String phoneNumber;
        private final Map<String, String> metadata = new LinkedHashMap<>();
        private final Map<String, String> fields = new LinkedHashMap<>();
        private String requestRrn;

        private Builder() {
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder language(Language language) {
            this.language = language;
            return this;
        }

        public Builder operation(Operation operation) {
            this.operation = operation;
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

        public Builder redirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
            return this;
        }

        public Builder cardSave(boolean cardSave) {
            this.cardSave = cardSave;
            return this;
        }

        public Builder threeDS(boolean threeDS) {
            this.threeDS = threeDS;
            return this;
        }

        public Builder autoPaymentType(AutoPaymentType autoPaymentType) {
            this.autoPaymentType = autoPaymentType;
            return this;
        }

        public Builder installment(Installment installment) {
            this.installment = installment;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder metadata(String key, String value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder metadata(Map<String, String> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        public Builder field(String key, String value) {
            this.fields.put(key, value);
            return this;
        }

        public Builder requestRrn(String requestRrn) {
            this.requestRrn = requestRrn;
            return this;
        }

        public CreateOrderRequest build() {
            return new CreateOrderRequest(this);
        }
    }
}