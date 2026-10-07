package com.payriff.sdk.model;

public final class CardDetails {

    private String maskedPan;
    private String brand;
    private String uuid;
    private String cardHolderName;
    private String phoneNumber;

    public String getMaskedPan() {
        return maskedPan;
    }

    public String getBrand() {
        return brand;
    }

    public String getUuid() {
        return uuid;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}