package com.payriff.sdk.model;

public final class TransactionResponse {

    private boolean redirect;
    private String redirectUrl;
    private boolean threeDS;
    private String accessUrl;
    private String channel;
    private DirectPayResponse transactionResult;

    public boolean isRedirect() {
        return redirect;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public boolean isThreeDS() {
        return threeDS;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public String getChannel() {
        return channel;
    }

    public DirectPayResponse getTransactionResult() {
        return transactionResult;
    }
}