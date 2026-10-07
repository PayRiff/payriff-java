package com.payriff.sdk.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public final class AutoPayResponse {

    private String orderId;
    private String paymentUrl;
    private String description;
    private BigDecimal amount;
    private BigDecimal commission;
    private BigDecimal commissionRate;
    private Currency currencyType;
    private Operation operationType;
    private PaymentStatus paymentStatus;
    private boolean auto;
    private LocalDateTime createdDate;
    private List<OrderTransaction> transactions;
    private TransactionResponse transactionResponseDto;

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public Currency getCurrency() {
        return currencyType;
    }

    public Operation getOperationType() {
        return operationType;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public boolean isAuto() {
        return auto;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public List<OrderTransaction> getTransactions() {
        return transactions == null ? Collections.emptyList() : Collections.unmodifiableList(transactions);
    }

    public TransactionResponse getTransactionResponse() {
        return transactionResponseDto;
    }
}