package com.payriff.sdk;

import com.payriff.sdk.internal.ApiRequest;
import com.payriff.sdk.internal.HttpTransport;
import com.payriff.sdk.model.CompleteRequest;
import com.payriff.sdk.model.CreateOrderRequest;
import com.payriff.sdk.model.CreateOrderResponse;
import com.payriff.sdk.model.OrderInfo;
import com.payriff.sdk.model.RefundRequest;

import java.util.Objects;

import static com.payriff.sdk.internal.ApiRequest.Method.GET;
import static com.payriff.sdk.internal.ApiRequest.Method.PATCH;
import static com.payriff.sdk.internal.ApiRequest.Method.POST;
import static com.payriff.sdk.internal.PathSegment.encode;

public final class OrdersApi {

    private final HttpTransport transport;

    OrdersApi(HttpTransport transport) {
        this.transport = transport;
    }

    public CreateOrderResponse create(CreateOrderRequest request) {
        Objects.requireNonNull(request, "request");
        return transport.execute(ApiRequest.builder(POST, "/api/v3/orders")
                .header("X-REQUEST-RRN", request.getRequestRrn())
                .body(request)
                .payload(CreateOrderResponse.class)
                .build());
    }

    public OrderInfo get(String orderId) {
        return transport.execute(ApiRequest.builder(GET, "/api/v3/orders/" + encode(orderId, "orderId"))
                .payload(OrderInfo.class)
                .build());
    }

    public OrderInfo getStatus(String orderId) {
        return transport.execute(ApiRequest.builder(GET, "/api/v3/orders/" + encode(orderId, "orderId") + "/status")
                .payload(OrderInfo.class)
                .build());
    }

    public OrderInfo getByRequestRrn(String requestRrn) {
        return transport.execute(ApiRequest.builder(GET, "/api/v3/orders/" + encode(requestRrn, "requestRrn") + "/rrn")
                .payload(OrderInfo.class)
                .build());
    }

    public void expire(String orderId) {
        encode(orderId, "orderId");
        transport.execute(ApiRequest.builder(PATCH, "/api/v3/expire-status")
                .query("orderId", orderId)
                .build());
    }

    public void refund(RefundRequest request) {
        Objects.requireNonNull(request, "request");
        transport.execute(ApiRequest.builder(POST, "/api/v3/refund").body(request).build());
    }

    public void complete(CompleteRequest request) {
        Objects.requireNonNull(request, "request");
        transport.execute(ApiRequest.builder(POST, "/api/v3/complete").body(request).build());
    }

    public byte[] downloadReceipt(String orderIdOrRrn) {
        return transport.download(ApiRequest.builder(GET, "/api/v3/acquiring/receipt/" + encode(orderIdOrRrn, "orderIdOrRrn")).build());
    }
}