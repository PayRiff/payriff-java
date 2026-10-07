package com.payriff.sdk.model;

import java.time.LocalDateTime;

public final class SavedCard {

    private String cardUuid;
    private String maskedPan;
    private String cardBrand;
    private LocalDateTime createdDate;

    public String getCardUuid() {
        return cardUuid;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
}