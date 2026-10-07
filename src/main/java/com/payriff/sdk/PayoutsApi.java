package com.payriff.sdk;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.payriff.sdk.internal.ApiRequest;
import com.payriff.sdk.internal.HttpTransport;
import com.payriff.sdk.model.Page;
import com.payriff.sdk.model.PayoutFilter;
import com.payriff.sdk.model.PayoutRequest;
import com.payriff.sdk.model.PayoutResult;
import com.payriff.sdk.model.PayoutStatus;
import com.payriff.sdk.model.PayoutSummary;

import java.util.Collections;
import java.util.Objects;

import static com.payriff.sdk.internal.ApiRequest.Method.GET;
import static com.payriff.sdk.internal.ApiRequest.Method.POST;
import static com.payriff.sdk.internal.PathSegment.encode;

public final class PayoutsApi {

    private final HttpTransport transport;

    PayoutsApi(HttpTransport transport) {
        this.transport = transport;
    }

    public PayoutResult create(PayoutRequest request) {
        Objects.requireNonNull(request, "request");
        return transport.execute(ApiRequest.builder(POST, "/api/v3/payout")
                .header("X-IDEMPOTENCY-KEY", request.getIdempotencyKey())
                .body(request)
                .merchantEnvelope()
                .payload(PayoutResult.class)
                .build());
    }

    public PayoutStatus getByRequestRrn(String requestRrn) {
        return transport.execute(ApiRequest.builder(GET, "/api/v3/payout/info/" + encode(requestRrn, "requestRrn"))
                .payload(PayoutStatus.class)
                .build());
    }

    public String checkCardholder(String cardPan) {
        Objects.requireNonNull(cardPan, "cardPan");
        String pan = cardPan.replaceAll("[\\s-]", "");
        if (!pan.matches("\\d{16}")) {
            throw new IllegalArgumentException("cardPan must be a 16-digit number");
        }
        return transport.execute(ApiRequest.builder(POST, "/api/v3/payout/check-cardholder")
                .body(Collections.singletonMap("cardPan", pan))
                .payload(String.class)
                .build());
    }

    public Page<PayoutSummary> list(PayoutFilter filter) {
        Objects.requireNonNull(filter, "filter");
        return transport.execute(ApiRequest.builder(GET, "/api/v3/payouts")
                .query(filter.toQuery())
                .payload(TypeFactory.defaultInstance().constructParametricType(Page.class, PayoutSummary.class))
                .build());
    }

    public byte[] downloadReceipt(String requestRrn) {
        return transport.download(ApiRequest.builder(GET, "/api/v3/payout/receipt/" + encode(requestRrn, "requestRrn")).build());
    }
}