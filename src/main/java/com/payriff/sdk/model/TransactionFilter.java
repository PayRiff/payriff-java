package com.payriff.sdk.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TransactionFilter {

    public static final int MAX_PAGE_SIZE = 20;
    static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final Map<String, String> params;

    private TransactionFilter(Builder b) {
        this.params = new LinkedHashMap<>(b.params);
        params.put("page", String.valueOf(b.page));
        params.put("offset", String.valueOf(b.size));
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, String> toQuery() {
        return new LinkedHashMap<>(params);
    }

    public static final class Builder {
        private final Map<String, String> params = new LinkedHashMap<>();
        private int page = 0;
        private int size = 10;

        private Builder() {
        }

        public Builder orderId(String orderId) {
            return put("orderId", orderId);
        }

        public Builder rrn(String rrn) {
            return put("rrn", rrn);
        }

        public Builder status(PaymentStatus status) {
            return put("status", status == null ? null : status.name());
        }

        public Builder amount(String amount) {
            return put("amount", amount);
        }

        public Builder description(String description) {
            return put("description", description);
        }

        public Builder name(String name) {
            return put("name", name);
        }

        public Builder fullName(String fullName) {
            return put("fullName", fullName);
        }

        public Builder cardNumber(String cardNumber) {
            return put("cardNumber", cardNumber);
        }

        public Builder bookingId(String bookingId) {
            return put("bookingId", bookingId);
        }

        public Builder invoiceCode(String invoiceCode) {
            return put("invoiceCode", invoiceCode);
        }

        public Builder from(LocalDate from) {
            return put("from", from == null ? null : DATE.format(from));
        }

        public Builder to(LocalDate to) {
            return put("to", to == null ? null : DATE.format(to));
        }

        public Builder page(int page) {
            if (page < 0) {
                throw new IllegalArgumentException("page must be >= 0");
            }
            this.page = page;
            return this;
        }

        public Builder size(int size) {
            if (size < 1 || size > MAX_PAGE_SIZE) {
                throw new IllegalArgumentException("size must be 1-" + MAX_PAGE_SIZE);
            }
            this.size = size;
            return this;
        }

        private Builder put(String name, String value) {
            if (value == null) {
                params.remove(name);
            } else {
                params.put(name, value);
            }
            return this;
        }

        public TransactionFilter build() {
            return new TransactionFilter(this);
        }
    }
}