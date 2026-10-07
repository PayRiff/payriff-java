package com.payriff.sdk.model;

import java.math.BigDecimal;

public final class PayoutStatus {

    private String bankName;
    private TransferState state;
    private String cardPan;
    private BigDecimal transferAmount;
    private String createdDate;
    private String formattedDate;
    private String transferType;
    private String merchant;
    private String description;
    private String fullName;
    private String finCode;

    public String getBankName() {
        return bankName;
    }

    public TransferState getState() {
        return state;
    }

    public String getCardPan() {
        return cardPan;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public String getTransferType() {
        return transferType;
    }

    public String getMerchant() {
        return merchant;
    }

    public String getDescription() {
        return description;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFinCode() {
        return finCode;
    }
}