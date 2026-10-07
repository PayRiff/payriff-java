package com.payriff.sdk.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class CardSaveDetails {

    private String cardSaveId;
    private String orderId;
    private CardSaveStatus status;
    private String cardUuid;
    private String maskedPan;
    private String cardBrand;
    private BigDecimal amount;
    private Currency currency;
    private String customerRef;
    private LocalDateTime createdDate;
    private LocalDateTime verifiedDate;

    public String getCardSaveId() {
        return cardSaveId;
    }

    public String getOrderId() {
        return orderId;
    }

    public CardSaveStatus getStatus() {
        return status;
    }

    public String getCardUuid() {
        return cardUuid;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getCustomerRef() {
        return customerRef;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getVerifiedDate() {
        return verifiedDate;
    }
}