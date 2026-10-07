package com.payriff.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class CardSaveRequest {

    private final String customerRef;
    private final String callbackUrl;
    private final String description;
    private final Language language;
    private final Map<String, String> metadata;
    @JsonIgnore
    private final String idempotencyKey;

    private CardSaveRequest(Builder b) {
        this.customerRef = Objects.requireNonNull(b.customerRef, "customerRef");
        this.callbackUrl = Objects.requireNonNull(b.callbackUrl, "callbackUrl");
        this.description = b.description;
        this.language = b.language;
        this.metadata = b.metadata.isEmpty() ? null : new LinkedHashMap<>(b.metadata);
        this.idempotencyKey = b.idempotencyKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public static final class Builder {
        private String customerRef;
        private String callbackUrl;
        private String description;
        private Language language;
        private final Map<String, String> metadata = new LinkedHashMap<>();
        private String idempotencyKey;

        private Builder() {
        }

        public Builder customerRef(String customerRef) {
            this.customerRef = customerRef;
            return this;
        }

        public Builder callbackUrl(String callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder language(Language language) {
            this.language = language;
            return this;
        }

        public Builder metadata(String key, String value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public CardSaveRequest build() {
            return new CardSaveRequest(this);
        }
    }
}