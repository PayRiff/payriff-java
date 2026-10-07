package com.payriff.sdk;

import com.fasterxml.jackson.databind.JsonNode;
import com.payriff.sdk.internal.Json;
import com.payriff.sdk.model.OrderInfo;

import java.io.IOException;
import java.util.Objects;

public final class Webhooks {

    private Webhooks() {
    }

    public static OrderInfo parseOrderCallback(String body) {
        Objects.requireNonNull(body, "body");
        try {
            JsonNode root = Json.mapper().readTree(body);
            JsonNode payload = root == null ? null : root.get("payload");
            if (payload == null || !payload.isObject() || !payload.hasNonNull("orderId")) {
                throw new IllegalArgumentException("Not a Payriff order callback");
            }
            return Json.mapper().treeToValue(payload, OrderInfo.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Not a Payriff order callback", e);
        }
    }
}