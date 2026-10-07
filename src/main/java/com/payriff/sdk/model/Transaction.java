package com.payriff.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public final class Transaction {

    private Long id;
    private Long applicationId;
    private String orderId;
    private String sessionId;
    private String uuid;
    private String rrn;
    private String externalRrn;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal refundAmount;
    private BigDecimal restOfAmount;
    private BigDecimal amountWithoutFee;
    private BigDecimal payriffAmount;
    private BigDecimal commissionRate;
    @JsonProperty("extra_payment")
    private BigDecimal extraPayment;
    private Currency currencyType;
    private PaymentStatus paymentStatus;
    private String paymentSource;
    private String description;
    private String responseDescription;
    private Language orderLanguage;
    private String tariffType;
    private String source;
    private String fullName;
    private String phoneNumber;
    private String bookingId;
    private String invoiceCode;
    private String pan;
    @JsonProperty("card_brand")
    private String cardBrand;
    @JsonProperty("payment_route")
    private String paymentRoute;
    @JsonProperty("payment_way")
    private String paymentWay;
    private UUID transitId;
    private String createdDate;
    private String lastModifiedDate;

    public Long getId() {
        return id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getRrn() {
        return rrn;
    }

    public String getExternalRrn() {
        return externalRrn;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public BigDecimal getRestOfAmount() {
        return restOfAmount;
    }

    public BigDecimal getAmountWithoutFee() {
        return amountWithoutFee;
    }

    public BigDecimal getPayriffAmount() {
        return payriffAmount;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public BigDecimal getExtraPayment() {
        return extraPayment;
    }

    public Currency getCurrency() {
        return currencyType;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentSource() {
        return paymentSource;
    }

    public String getDescription() {
        return description;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public Language getOrderLanguage() {
        return orderLanguage;
    }

    public String getTariffType() {
        return tariffType;
    }

    public String getSource() {
        return source;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public String getPan() {
        return pan;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public String getPaymentRoute() {
        return paymentRoute;
    }

    public String getPaymentWay() {
        return paymentWay;
    }

    public UUID getTransitId() {
        return transitId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }
}