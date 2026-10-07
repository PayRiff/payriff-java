package com.payriff.sdk;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.payriff.sdk.internal.ApiRequest;
import com.payriff.sdk.internal.CardEncryptor;
import com.payriff.sdk.internal.HttpTransport;
import com.payriff.sdk.internal.Json;
import com.payriff.sdk.model.AutoPayRequest;
import com.payriff.sdk.model.AutoPayResponse;
import com.payriff.sdk.model.DirectPayRequest;
import com.payriff.sdk.model.DirectPayResponse;

import java.util.Objects;

import static com.payriff.sdk.internal.ApiRequest.Method.POST;

public final class PaymentsApi {

    private final HttpTransport transport;
    private final CardEncryptor cardEncryptor;

    PaymentsApi(HttpTransport transport, CardEncryptor cardEncryptor) {
        this.transport = transport;
        this.cardEncryptor = cardEncryptor;
    }

    public DirectPayResponse directPay(DirectPayRequest request) {
        Objects.requireNonNull(request, "request");
        CardEncryptor.EncryptedCard encrypted = cardEncryptor.encrypt(request.getCard());

        ObjectNode body = Json.mapper().valueToTree(request);
        ObjectNode paymentData = body.putObject("paymentData");
        paymentData.put("paymentWay", "DIRECT");
        paymentData.put("encryptedMessage", encrypted.encryptedMessage());
        paymentData.put("cardSave", request.isCardSave());

        return transport.execute(ApiRequest.builder(POST, "/api/v3/directPay")
                .header("X-REQUEST-RRN", request.getRequestRrn())
                .header("x-secret-key", encrypted.secretKey())
                .body(body)
                .payload(DirectPayResponse.class)
                .build());
    }

    public AutoPayResponse autoPay(AutoPayRequest request) {
        Objects.requireNonNull(request, "request");
        return transport.execute(ApiRequest.builder(POST, "/api/v3/autoPay")
                .header("X-REQUEST-RRN", request.getRequestRrn())
                .body(request)
                .payload(AutoPayResponse.class)
                .build());
    }
}