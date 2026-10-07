package com.payriff.sdk;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.payriff.sdk.internal.ApiRequest;
import com.payriff.sdk.internal.HttpTransport;
import com.payriff.sdk.model.Page;
import com.payriff.sdk.model.Transaction;
import com.payriff.sdk.model.TransactionFilter;

import java.util.Objects;

import static com.payriff.sdk.internal.ApiRequest.Method.GET;

public final class TransactionsApi {

    private final HttpTransport transport;

    TransactionsApi(HttpTransport transport) {
        this.transport = transport;
    }

    public Page<Transaction> list(TransactionFilter filter) {
        Objects.requireNonNull(filter, "filter");
        return transport.execute(ApiRequest.builder(GET, "/api/v3/transactions")
                .query(filter.toQuery())
                .payload(TypeFactory.defaultInstance().constructParametricType(Page.class, Transaction.class))
                .build());
    }
}