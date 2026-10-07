package com.payriff.sdk.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class PayoutSummary {

    private Long id;
    private String requestRrn;
    private BigDecimal transferAmount;
    private BigDecimal amountWithFee;
    private BigDecimal fee;
    private LocalDateTime createdDate;
    private TransferState state;
    private String stateDescription;
    private String cardPan;
    private String finCode;
    private String fullName;
    private String description;

    public Long getId() {
        return id;
    }

    public String getRequestRrn() {
        return requestRrn;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public BigDecimal getAmountWithFee() {
        return amountWithFee;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public TransferState getState() {
        return state;
    }

    public String getStateDescription() {
        return stateDescription;
    }

    public String getCardPan() {
        return cardPan;
    }

    public String getFinCode() {
        return finCode;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDescription() {
        return description;
    }
}