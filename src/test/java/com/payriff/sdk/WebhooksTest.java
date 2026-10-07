package com.payriff.sdk;

import com.payriff.sdk.model.OrderInfo;
import com.payriff.sdk.model.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WebhooksTest {

    @Test
    void parsesGsonSerializedCallback() {
        String body = "{\"payload\":{\"orderId\":\"ORD-1\",\"invoiceUuid\":\"inv-1\",\"amount\":10.5,"
                + "\"currencyType\":\"AZN\",\"paymentStatus\":\"APPROVED\",\"operationType\":\"PURCHASE\",\"auto\":false,"
                + "\"createdDate\":\"2026-10-01T14:05:09.123\",\"customFields\":{\"x\":\"y\"},"
                + "\"transactions\":[{\"uuid\":\"6f1c2a4e-0b7d-4c3e-9a51-2d8e7f6b1c90\",\"status\":\"APPROVED\","
                + "\"createdDate\":\"2026-10-01T14:05:10.000\"}]},"
                + "\"code\":\"00000\",\"message\":\"Operation performed successfully\",\"route\":\"/dashboard\",\"responseId\":\"http-nio-1\"}";

        OrderInfo order = Webhooks.parseOrderCallback(body);

        assertThat(order.getOrderId()).isEqualTo("ORD-1");
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(order.getInvoiceUuid()).isEqualTo("inv-1");
        assertThat(order.getCreatedDate()).isEqualTo(LocalDateTime.of(2026, 10, 1, 14, 5, 9, 123000000));
        assertThat(order.getTransactions()).hasSize(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "not json", "{}", "{\"payload\":null}", "{\"payload\":{\"amount\":1}}", "[]"})
    void rejectsNonCallbackBodies(String body) {
        assertThatThrownBy(() -> Webhooks.parseOrderCallback(body))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not a Payriff order callback");
    }
}