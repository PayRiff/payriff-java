package com.payriff.sdk.model;

import java.time.LocalDateTime;
import java.util.UUID;

public final class OrderTransaction {

    private UUID uuid;
    private LocalDateTime createdDate;
    private String status;
    private String channel;
    private String channelType;
    private String requestRrn;
    private String responseRrn;
    private String externalRrn;
    private String pan;
    private String paymentWay;
    private CardDetails cardDetails;
    private UUID cardUuid;
    private Integer recurrenceId;
    private String responseDescription;
    private String merchantCategory;
    private Installment installment;

    public UUID getUuid() {
        return uuid;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public String getStatus() {
        return status;
    }

    public String getChannel() {
        return channel;
    }

    public String getChannelType() {
        return channelType;
    }

    public String getRequestRrn() {
        return requestRrn;
    }

    public String getResponseRrn() {
        return responseRrn;
    }

    public String getExternalRrn() {
        return externalRrn;
    }

    public String getPan() {
        return pan;
    }

    public String getPaymentWay() {
        return paymentWay;
    }

    public CardDetails getCardDetails() {
        return cardDetails;
    }

    public UUID getCardUuid() {
        return cardUuid;
    }

    public Integer getRecurrenceId() {
        return recurrenceId;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public String getMerchantCategory() {
        return merchantCategory;
    }

    public Installment getInstallment() {
        return installment;
    }
}