package com.payriff.sdk;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.payriff.sdk.model.CompleteRequest;
import com.payriff.sdk.model.CreateOrderRequest;
import com.payriff.sdk.model.CreateOrderResponse;
import com.payriff.sdk.model.Currency;
import com.payriff.sdk.model.Installment;
import com.payriff.sdk.model.InstallmentPeriod;
import com.payriff.sdk.model.InstallmentProductType;
import com.payriff.sdk.model.Language;
import com.payriff.sdk.model.Operation;
import com.payriff.sdk.model.OrderInfo;
import com.payriff.sdk.model.OrderTransaction;
import com.payriff.sdk.model.PaymentStatus;
import com.payriff.sdk.model.RefundRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.patchRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WireMockTest
class OrdersApiTest {

    static final String OK_VOID = "{\"code\":\"00000\",\"message\":\"Operation performed successfully\",\"payload\":null}";

    static final String ORDER_INFO = "{\"code\":\"00000\",\"message\":\"Operation performed successfully\",\"payload\":{"
            + "\"orderId\":\"ORD-1\",\"amount\":10.50,\"currencyType\":\"AZN\",\"merchantName\":\"Shop\","
            + "\"commission\":0.20,\"commissionRate\":2.00,\"operationType\":\"PURCHASE\",\"paymentStatus\":\"APPROVED\","
            + "\"auto\":false,\"createdDate\":\"2026-10-01T14:05:09.123456\",\"description\":\"Order #1\","
            + "\"metadata\":\"{\\\"k\\\":\\\"v\\\"}\",\"transactions\":[{"
            + "\"uuid\":\"6f1c2a4e-0b7d-4c3e-9a51-2d8e7f6b1c90\",\"createdDate\":\"2026-10-01T14:05:10.000001\","
            + "\"status\":\"APPROVED\",\"channel\":\"KAPITAL_BANK\",\"requestRrn\":\"req-1\",\"responseRrn\":\"resp-1\","
            + "\"pan\":\"416974******1979\",\"paymentWay\":\"DIRECT\","
            + "\"cardDetails\":{\"maskedPan\":\"416974******1979\",\"brand\":\"VISA\",\"bcryptedCardPan\":\"$2a$x\"},"
            + "\"installment\":{\"type\":\"BIRKART\",\"period\":\"PERIOD_3\"}}]}}";

    private PayriffClient client;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wm) {
        client = PayriffClient.builder().appKey("app-key").baseUrl(URI.create(wm.getHttpBaseUrl())).build();
    }

    @Test
    void createSendsBodyAndRrnHeader() {
        stubFor(post("/api/v3/orders").willReturn(okJson("{\"code\":\"00000\",\"payload\":{"
                + "\"orderId\":\"ORD-1\",\"paymentUrl\":\"https://pay.payriff.com/ORD-1\",\"transactionId\":77,"
                + "\"comissionRate\":2.5,\"amount\":10.00,\"fee\":0.25,\"totalAmount\":10.25}}")));

        CreateOrderResponse response = client.orders().create(CreateOrderRequest.builder()
                .amount(new BigDecimal("10.00"))
                .language(Language.AZ)
                .description("Order #1")
                .callbackUrl("https://shop.az/cb")
                .installment(new Installment(InstallmentProductType.BIRKART, InstallmentPeriod.PERIOD_3))
                .metadata("cartId", "c-9")
                .requestRrn("rrn-1")
                .build());

        verify(postRequestedFor(urlEqualTo("/api/v3/orders"))
                .withHeader("Authorization", equalTo("app-key"))
                .withHeader("X-REQUEST-RRN", equalTo("rrn-1"))
                .withRequestBody(equalToJson("{\"amount\":10.00,\"currency\":\"AZN\",\"language\":\"AZ\","
                        + "\"operation\":\"PURCHASE\",\"description\":\"Order #1\",\"callbackUrl\":\"https://shop.az/cb\","
                        + "\"installment\":{\"type\":\"BIRKART\",\"period\":\"PERIOD_3\"},\"metadata\":{\"cartId\":\"c-9\"}}")));
        assertThat(response.getOrderId()).isEqualTo("ORD-1");
        assertThat(response.getPaymentUrl()).isEqualTo("https://pay.payriff.com/ORD-1");
        assertThat(response.getTransactionId()).isEqualTo(77L);
        assertThat(response.getCommissionRate()).isEqualByComparingTo("2.5");
        assertThat(response.getTotalAmount()).isEqualByComparingTo("10.25");
    }

    @Test
    void createOmitsRrnHeaderWhenAbsent() {
        stubFor(post("/api/v3/orders").willReturn(okJson("{\"code\":\"00000\",\"payload\":{\"orderId\":\"ORD-1\"}}")));

        client.orders().create(CreateOrderRequest.builder().amount(BigDecimal.ONE).build());

        verify(postRequestedFor(urlEqualTo("/api/v3/orders")).withHeader("X-REQUEST-RRN", absent()));
    }

    static Stream<Arguments> lookups() {
        BiFunction<OrdersApi, String, OrderInfo> get = OrdersApi::get;
        BiFunction<OrdersApi, String, OrderInfo> status = OrdersApi::getStatus;
        BiFunction<OrdersApi, String, OrderInfo> byRrn = OrdersApi::getByRequestRrn;
        return Stream.of(
                Arguments.of(get, "ORD-1", "/api/v3/orders/ORD-1"),
                Arguments.of(status, "ORD-1", "/api/v3/orders/ORD-1/status"),
                Arguments.of(byRrn, "rrn 1/2", "/api/v3/orders/rrn%201%2F2/rrn"));
    }

    @ParameterizedTest
    @MethodSource("lookups")
    void lookupsParseOrderInfo(BiFunction<OrdersApi, String, OrderInfo> call, String id, String path) {
        stubFor(get(urlEqualTo(path)).willReturn(okJson(ORDER_INFO)));

        OrderInfo order = call.apply(client.orders(), id);

        assertThat(order.getOrderId()).isEqualTo("ORD-1");
        assertThat(order.getAmount()).isEqualByComparingTo("10.50");
        assertThat(order.getCurrency()).isEqualTo(Currency.AZN);
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(order.getOperationType()).isEqualTo(Operation.PURCHASE);
        assertThat(order.getCreatedDate()).isEqualTo(LocalDateTime.of(2026, 10, 1, 14, 5, 9, 123456000));
        assertThat(order.getMetadata()).isEqualTo("{\"k\":\"v\"}");

        OrderTransaction tx = order.getTransactions().get(0);
        assertThat(tx.getUuid()).isEqualTo(UUID.fromString("6f1c2a4e-0b7d-4c3e-9a51-2d8e7f6b1c90"));
        assertThat(tx.getChannel()).isEqualTo("KAPITAL_BANK");
        assertThat(tx.getCardDetails().getMaskedPan()).isEqualTo("416974******1979");
        assertThat(tx.getCardDetails().getBrand()).isEqualTo("VISA");
        assertThat(tx.getInstallment().getPeriod()).isEqualTo(InstallmentPeriod.PERIOD_3);
    }

    @Test
    void unknownEnumValuesDoNotBreakParsing() {
        stubFor(get(urlEqualTo("/api/v3/orders/ORD-1")).willReturn(okJson("{\"code\":\"00000\",\"payload\":{"
                + "\"orderId\":\"ORD-1\",\"paymentStatus\":\"SOMETHING_NEW\",\"currencyType\":\"GBP\",\"transactions\":null}}")));

        OrderInfo order = client.orders().get("ORD-1");

        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.UNKNOWN);
        assertThat(order.getCurrency()).isNull();
        assertThat(order.getTransactions()).isEmpty();
    }

    @Test
    void expireSendsOrderIdAsQuery() {
        stubFor(patch(urlEqualTo("/api/v3/expire-status?orderId=ORD-1")).willReturn(okJson(OK_VOID)));

        client.orders().expire("ORD-1");

        verify(patchRequestedFor(urlEqualTo("/api/v3/expire-status?orderId=ORD-1")));
    }

    @Test
    void refundSendsBody() {
        stubFor(post("/api/v3/refund").willReturn(okJson(OK_VOID)));

        client.orders().refund(RefundRequest.builder("ORD-1").amount(new BigDecimal("5.00")).refundReason("damaged").build());

        verify(postRequestedFor(urlEqualTo("/api/v3/refund"))
                .withRequestBody(equalToJson("{\"orderId\":\"ORD-1\",\"amount\":5.00,\"refundReason\":\"damaged\"}")));
    }

    @Test
    void completeSendsBody() {
        stubFor(post("/api/v3/complete").willReturn(okJson(OK_VOID)));

        client.orders().complete(CompleteRequest.builder("ORD-1").amount(new BigDecimal("7.00")).build());

        verify(postRequestedFor(urlEqualTo("/api/v3/complete"))
                .withRequestBody(equalToJson("{\"orderId\":\"ORD-1\",\"amount\":7.00}")));
    }

    @Test
    void blankOrderIdIsRejectedBeforeSending() {
        assertThatThrownBy(() -> client.orders().get(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("orderId must not be blank");
    }

    @Test
    void missingAmountIsRejected() {
        assertThatThrownBy(() -> CreateOrderRequest.builder().build())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("amount");
    }
}