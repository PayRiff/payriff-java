package com.payriff.sdk.model;

import java.util.Objects;

public final class Installment {

    private InstallmentProductType type;
    private InstallmentPeriod period;

    private Installment() {
    }

    public Installment(InstallmentProductType type, InstallmentPeriod period) {
        this.type = Objects.requireNonNull(type, "type");
        this.period = Objects.requireNonNull(period, "period");
    }

    public InstallmentProductType getType() {
        return type;
    }

    public InstallmentPeriod getPeriod() {
        return period;
    }
}