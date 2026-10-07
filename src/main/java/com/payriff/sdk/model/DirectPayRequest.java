package com.payriff.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class DirectPayRequest {

    private final BigDecimal amount;
    private final Operation operation;
    private final Currency currency;
    private final String description;
    private final String callbackUrl;
    private final Boolean threeDS;
    private final Map<String, String> customFields;
    @JsonIgnore
    private final CardData card;
    @JsonIgnore
    private final boolean cardSave;
    @JsonIgnore
    private final String requestRrn;

    private DirectPayRequest(Builder b) {
        this.amount = Objects.requireNonNull(b.amount, "amount");
        this.operation = Objects.requireNonNull(b.operation, "operation");
        this.currency = Objects.requireNonNull(b.currency, "currency");
        this.description = Objects.requireNonNull(b.description, "description");
        this.callbackUrl = b.callbackUrl;
        this.threeDS = b.threeDS;
        this.customFields = b.customFields.isEmpty() ? null : new LinkedHashMap<>(b.customFields);
        this.card = Objects.requireNonNull(b.card, "card");
        this.cardSave = b.cardSave;
        this.requestRrn = b.requestRrn;
    }

    public static Builder builder() {
        return new Builder();
    }

    public CardData getCard() {
        return card;
    }

    public boolean isCardSave() {
        return cardSave;
    }

    public String getRequestRrn() {
        return requestRrn;
    }

    public static final class Builder {
        private BigDecimal amount;
        private Operation operation = Operation.PURCHASE;
        private Currency currency = Currency.AZN;
        private String description;
        private String callbackUrl;
        private Boolean threeDS;
        private final Map<String, String> customFields = new LinkedHashMap<>();
        private CardData card;
        private boolean cardSave;
        private String requestRrn;

        private Builder() {
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

        public Builder customField(String key, String value) {
            this.customFields.put(key, value);
            return this;
        }

        public Builder card(CardData card) {
            this.card = card;
            return this;
        }

        public Builder cardSave(boolean cardSave) {
            this.cardSave = cardSave;
            return this;
        }

        public Builder requestRrn(String requestRrn) {
            this.requestRrn = requestRrn;
            return this;
        }

        public DirectPayRequest build() {
            return new DirectPayRequest(this);
        }
    }
}