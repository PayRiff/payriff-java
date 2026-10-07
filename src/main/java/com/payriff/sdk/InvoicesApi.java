package com.payriff.sdk;

import com.payriff.sdk.internal.ApiRequest;
import com.payriff.sdk.internal.HttpTransport;
import com.payriff.sdk.model.Invoice;
import com.payriff.sdk.model.InvoiceCreateRequest;
import com.payriff.sdk.model.InvoiceDetails;

import java.util.Collections;
import java.util.Objects;

import static com.payriff.sdk.internal.ApiRequest.Method.POST;
import static com.payriff.sdk.internal.PathSegment.encode;

public final class InvoicesApi {

    private final HttpTransport transport;

    InvoicesApi(HttpTransport transport) {
        this.transport = transport;
    }

    public Invoice create(InvoiceCreateRequest request) {
        Objects.requireNonNull(request, "request");
        return transport.execute(ApiRequest.builder(POST, "/api/v2/invoices")
                .body(request)
                .merchantEnvelope()
                .payload(Invoice.class)
                .build());
    }

    public InvoiceDetails get(String invoiceUuid) {
        encode(invoiceUuid, "invoiceUuid");
        return transport.execute(ApiRequest.builder(POST, "/api/v2/get-invoice")
                .body(Collections.singletonMap("uuid", invoiceUuid))
                .merchantEnvelope()
                .payload(InvoiceDetails.class)
                .build());
    }
}