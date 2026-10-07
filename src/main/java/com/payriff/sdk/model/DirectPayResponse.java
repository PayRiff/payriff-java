package com.payriff.sdk.model;

public final class DirectPayResponse {

    private String orderId;
    private boolean threeDS;
    private boolean redirect;
    private String redirectUrl;
    private String paymentUrl;
    private OrderTransaction transactionResponse;

    public String getOrderId() {
        return orderId;
    }

    public boolean isThreeDS() {
        return threeDS;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public OrderTransaction getTransaction() {
        return transactionResponse;
    }
}