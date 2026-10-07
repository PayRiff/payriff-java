package com.payriff.sdk.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public final class OrderInfo {

    private String orderId;
    private String externalTransactionId;
    private String invoiceUuid;
    private BigDecimal amount;
    private Currency currencyType;
    private String merchantName;
    private BigDecimal commission;
    private BigDecimal commissionRate;
    private BigDecimal paidAmount;
    private BigDecimal extraPayment;
    private Operation operationType;
    private PaymentStatus paymentStatus;
    private boolean auto;
    private LocalDateTime createdDate;
    private String description;
    private String metadata;
    private String idempotencyKey;
    private List<OrderTransaction> transactions;

    public String getOrderId() {
        return orderId;
    }

    public String getExternalTransactionId() {
        return externalTransactionId;
    }

    public String getInvoiceUuid() {
        return invoiceUuid;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currencyType;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public BigDecimal getExtraPayment() {
        return extraPayment;
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

    public String getDescription() {
        return description;
    }

    public String getMetadata() {
        return metadata;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public List<OrderTransaction> getTransactions() {
        return transactions == null ? Collections.emptyList() : Collections.unmodifiableList(transactions);
    }
}