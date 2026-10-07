package com.payriff.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public final class CreateOrderResponse {

    private String orderId;
    private String sessionId;
    private String paymentUrl;
    private String previewUrl;
    private Long transactionId;
    @JsonProperty("comissionRate")
    private BigDecimal commissionRate;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal totalAmount;

    public String getOrderId() {
        return orderId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}