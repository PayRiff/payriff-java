package com.payriff.sdk.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class InvoiceDetails {

    private Long id;
    private String merchantId;
    private String uuid;
    private String invoiceUuid;
    private String invoiceCode;
    private InvoiceStatus invoiceStatus;
    private String baseUrl;
    private BigDecimal amount;
    private BigDecimal totalAmount;
    private BigDecimal payriffAmount;
    private BigDecimal payriffFee;
    private BigDecimal payriffFixedFeeAmount;
    private Currency currencyType;
    private Language languageType;
    private String paymentType;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String description;
    private LocalDateTime expireDate;
    private LocalDate paymentDay;
    private LocalDate expireDay;
    private LocalDateTime createdDate;
    private String approveURL;
    private String cancelURL;
    private String declineURL;
    private boolean active;
    private Integer installmentPeriod;
    private String source;
    private Boolean directPay;
    private String metadata;

    public Long getId() {
        return id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getInvoiceUuid() {
        return invoiceUuid;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public InvoiceStatus getStatus() {
        return invoiceStatus;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getPayriffAmount() {
        return payriffAmount;
    }

    public BigDecimal getPayriffFee() {
        return payriffFee;
    }

    public BigDecimal getPayriffFixedFeeAmount() {
        return payriffFixedFeeAmount;
    }

    public Currency getCurrency() {
        return currencyType;
    }

    public Language getLanguage() {
        return languageType;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public LocalDate getPaymentDay() {
        return paymentDay;
    }

    public LocalDate getExpireDay() {
        return expireDay;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public String getApproveUrl() {
        return approveURL;
    }

    public String getCancelUrl() {
        return cancelURL;
    }

    public String getDeclineUrl() {
        return declineURL;
    }

    public boolean isActive() {
        return active;
    }

    public Integer getInstallmentPeriod() {
        return installmentPeriod;
    }

    public String getSource() {
        return source;
    }

    public Boolean getDirectPay() {
        return directPay;
    }

    public String getMetadata() {
        return metadata;
    }
}