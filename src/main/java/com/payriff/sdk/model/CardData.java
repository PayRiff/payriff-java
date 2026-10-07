package com.payriff.sdk.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder({"pan", "cardHolder", "expiryYear", "expiryMonth", "cvv"})
public final class CardData {

    private final String pan;
    private final String cardHolder;
    private final String expiryYear;
    private final String expiryMonth;
    private final String cvv;

    private CardData(Builder b) {
        this.pan = digits(b.pan, "pan");
        if (pan.length() < 12 || pan.length() > 19) {
            throw new IllegalArgumentException("pan must contain 12-19 digits");
        }
        this.cardHolder = Objects.requireNonNull(b.cardHolder, "cardHolder").trim();
        this.expiryMonth = month(b.expiryMonth);
        this.expiryYear = year(b.expiryYear);
        this.cvv = digits(b.cvv, "cvv");
        if (cvv.length() < 3 || cvv.length() > 4) {
            throw new IllegalArgumentException("cvv must contain 3-4 digits");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPan() {
        return pan;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public String getCvv() {
        return cvv;
    }

    @Override
    public String toString() {
        return "CardData{pan=" + pan.substring(0, 6) + "******" + pan.substring(pan.length() - 4)
                + ", expiry=" + expiryMonth + "/" + expiryYear + "}";
    }

    private static String digits(String value, String field) {
        Objects.requireNonNull(value, field);
        String v = value.replaceAll("[\\s-]", "");
        if (!v.matches("\\d+")) {
            throw new IllegalArgumentException(field + " must contain digits only");
        }
        return v;
    }

    private static String month(String value) {
        int m = Integer.parseInt(digits(value, "expiryMonth"));
        if (m < 1 || m > 12) {
            throw new IllegalArgumentException("expiryMonth must be 1-12");
        }
        return String.format("%02d", m);
    }

    private static String year(String value) {
        String y = digits(value, "expiryYear");
        if (y.length() == 2) {
            return "20" + y;
        }
        if (y.length() != 4) {
            throw new IllegalArgumentException("expiryYear must have 2 or 4 digits");
        }
        return y;
    }

    public static final class Builder {
        private String pan;
        private String cardHolder;
        private String expiryYear;
        private String expiryMonth;
        private String cvv;

        private Builder() {
        }

        public Builder pan(String pan) {
            this.pan = pan;
            return this;
        }

        public Builder cardHolder(String cardHolder) {
            this.cardHolder = cardHolder;
            return this;
        }

        public Builder expiryYear(String expiryYear) {
            this.expiryYear = expiryYear;
            return this;
        }

        public Builder expiryMonth(String expiryMonth) {
            this.expiryMonth = expiryMonth;
            return this;
        }

        public Builder cvv(String cvv) {
            this.cvv = cvv;
            return this;
        }

        public CardData build() {
            return new CardData(this);
        }
    }
}