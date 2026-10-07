package com.payriff.sdk.model;

import java.math.BigDecimal;

public final class CardSaveResponse {

    private String cardSaveId;
    private String orderId;
    private String sessionId;
    private String paymentUrl;
    private BigDecimal amount;
    private Currency currency;
    private CardSaveStatus status;

    public String getCardSaveId() {
        return cardSaveId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public CardSaveStatus getStatus() {
        return status;
    }
}