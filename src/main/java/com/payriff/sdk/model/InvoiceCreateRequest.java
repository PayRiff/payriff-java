package com.payriff.sdk.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public final class InvoiceCreateRequest {

    private final BigDecimal amount;
    private final Boolean amountDynamic;
    private final Currency currencyType;
    private final Language languageType;
    private final String fullName;
    private final String email;
    private final String phoneNumber;
    private final String description;
    private final String customMessage;
    private final LocalDateTime expireDate;
    private final String approveURL;
    private final String cancelURL;
    private final String declineURL;
    private final String redirectURL;
    private final InstallmentProductType installmentProductType;
    private final Integer installmentPeriod;
    private final Boolean directPay;
    private final Boolean sendSms;
    private final Boolean sendWhatsapp;
    private final Boolean sendEmail;
    private final Map<String, String> metadata;
    private final String externalTransactionId;

    private InvoiceCreateRequest(Builder b) {
        if (!Boolean.TRUE.equals(b.amountDynamic) && b.amount == null) {
            throw new NullPointerException("amount");
        }
        this.amount = b.amount;
        this.amountDynamic = b.amountDynamic;
        this.currencyType = b.currency;
        this.languageType = b.language;
        this.fullName = b.fullName;
        this.email = b.email;
        this.phoneNumber = b.phoneNumber;
        this.description = b.description;
        this.customMessage = b.customMessage;
        this.expireDate = b.expireDate;
        this.approveURL = b.approveUrl;
        this.cancelURL = b.cancelUrl;
        this.declineURL = b.declineUrl;
        this.redirectURL = b.redirectUrl;
        this.installmentProductType = b.installmentProductType;
        this.installmentPeriod = b.installmentPeriod;
        this.directPay = b.directPay;
        this.sendSms = b.sendSms;
        this.sendWhatsapp = b.sendWhatsapp;
        this.sendEmail = b.sendEmail;
        this.metadata = b.metadata.isEmpty() ? null : new LinkedHashMap<>(b.metadata);
        this.externalTransactionId = b.externalTransactionId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private BigDecimal amount;
        private Boolean amountDynamic;
        private Currency currency = Currency.AZN;
        private Language language;
        private String fullName;
        private String email;
        private String phoneNumber;
        private String description;
        private String customMessage;
        private LocalDateTime expireDate;
        private String approveUrl;
        private String cancelUrl;
        private String declineUrl;
        private String redirectUrl;
        private InstallmentProductType installmentProductType;
        private Integer installmentPeriod;
        private Boolean directPay;
        private Boolean sendSms;
        private Boolean sendWhatsapp;
        private Boolean sendEmail;
        private final Map<String, String> metadata = new LinkedHashMap<>();
        private String externalTransactionId;

        private Builder() {
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder amountDynamic(boolean amountDynamic) {
            this.amountDynamic = amountDynamic;
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

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder customMessage(String customMessage) {
            this.customMessage = customMessage;
            return this;
        }

        public Builder expireDate(LocalDateTime expireDate) {
            this.expireDate = expireDate;
            return this;
        }

        public Builder approveUrl(String approveUrl) {
            this.approveUrl = approveUrl;
            return this;
        }

        public Builder cancelUrl(String cancelUrl) {
            this.cancelUrl = cancelUrl;
            return this;
        }

        public Builder declineUrl(String declineUrl) {
            this.declineUrl = declineUrl;
            return this;
        }

        public Builder redirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
            return this;
        }

        public Builder installment(InstallmentProductType type, int period) {
            this.installmentProductType = type;
            this.installmentPeriod = period;
            return this;
        }

        public Builder directPay(boolean directPay) {
            this.directPay = directPay;
            return this;
        }

        public Builder sendSms(boolean sendSms) {
            this.sendSms = sendSms;
            return this;
        }

        public Builder sendWhatsapp(boolean sendWhatsapp) {
            this.sendWhatsapp = sendWhatsapp;
            return this;
        }

        public Builder sendEmail(boolean sendEmail) {
            this.sendEmail = sendEmail;
            return this;
        }

        public Builder metadata(String key, String value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder externalTransactionId(String externalTransactionId) {
            this.externalTransactionId = externalTransactionId;
            return this;
        }

        public InvoiceCreateRequest build() {
            return new InvoiceCreateRequest(this);
        }
    }
}