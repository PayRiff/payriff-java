package com.payriff.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public final class PayoutResult {

    @JsonProperty("_final")
    private String finalState;
    private String state;
    private String stateDescription;
    private BigDecimal currentDepositBalance;
    private Long walletHistoryId;
    private String bankName;

    public String getFinalState() {
        return finalState;
    }

    public String getState() {
        return state;
    }

    public String getStateDescription() {
        return stateDescription;
    }

    public BigDecimal getCurrentDepositBalance() {
        return currentDepositBalance;
    }

    public Long getWalletHistoryId() {
        return walletHistoryId;
    }

    public String getBankName() {
        return bankName;
    }
}