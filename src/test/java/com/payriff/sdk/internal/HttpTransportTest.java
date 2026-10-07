package com.payriff.sdk.internal;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.payriff.sdk.exception.ApiException;
import com.payriff.sdk.exception.AuthenticationException;
import com.payriff.sdk.exception.InsufficientBalanceException;
import com.payriff.sdk.exception.PayoutLimitException;
import com.payriff.sdk.exception.PayriffConnectionException;
import com.payriff.sdk.exception.RequestRejectedException;
import com.payriff.sdk.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WireMockTest
class HttpTransportTest {

    private static final String APP_KEY = "test-app-key";
    private static final String MERCHANT_ID = "ES1000000";

    private HttpTransport transport;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wm) {
        transport = transport(wm.getHttpBaseUrl(), MERCHANT_ID);
    }

    @Test
    void unwrapsPayloadOnSuccess() {
        stubFor(get("/api/v3/orders/ORD-1").willReturn(okJson(
                "{\"code\":\"00000\",\"message\":\"Operation performed successfully\",\"responseId\":\"r-1\","
                        + "\"payload\":{\"orderId\":\"ORD-1\",\"amount\":10.50,\"unknownField\":true}}")));

        Sample result = transport.execute(ApiRequest.builder(ApiRequest.Method.GET, "/api/v3/orders/ORD-1")
                .payload(Sample.class).build());

        assertThat(result.orderId).isEqualTo("ORD-1");
        assertThat(result.amount).isEqualByComparingTo(new BigDecimal("10.50"));
    }

    @Test
    void sendsAuthAndClientHeaders() {
        stubFor(get("/api/v3/orders/ORD-1").willReturn(okJson("{\"code\":\"00000\",\"payload\":null}")));

        transport.execute(ApiRequest.builder(ApiRequest.Method.GET, "/api/v3/orders/ORD-1")
                .header("X-REQUEST-RRN", "rrn-1").build());

        verify(getRequestedFor(urlEqualTo("/api/v3/orders/ORD-1"))
                .withHeader("Authorization", equalTo(APP_KEY))
                .withHeader("X-REQUEST-RRN", equalTo("rrn-1"))
                .withHeader("User-Agent", matching("payriff-java/.+"))
                .withHeader("Content-Type", absent()));
    }

    @Test
    void omitsAuthorizationForUnauthenticatedRequests() {
        stubFor(get("/public").willReturn(okJson("{\"code\":\"00000\"}")));

        transport.execute(ApiRequest.builder(ApiRequest.Method.GET, "/public").unauthenticated().build());

        verify(getRequestedFor(urlEqualTo("/public")).withHeader("Authorization", absent()));
    }

    @Test
    void encodesQueryParametersAndSkipsNulls() {
        stubFor(get(urlPathEqualTo("/api/v3/transactions")).willReturn(okJson("{\"code\":\"00000\"}")));

        transport.execute(ApiRequest.builder(ApiRequest.Method.GET, "/api/v3/transactions")
                .query("description", "a b&c")
                .query("from", "01.09.2026")
                .query("status", null)
                .build());

        verify(getRequestedFor(urlEqualTo("/api/v3/transactions?description=a%20b%26c&from=01.09.2026")));
    }

    @Test
    void wrapsBodyInMerchantEnvelope() {
        stubFor(post("/api/v2/get-invoice").willReturn(okJson("{\"code\":\"00000\"}")));

        transport.execute(ApiRequest.builder(ApiRequest.Method.POST, "/api/v2/get-invoice")
                .body(Map.of("uuid", "inv-1")).merchantEnvelope().build());

        verify(postRequestedFor(urlEqualTo("/api/v2/get-invoice"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson("{\"merchant\":\"" + MERCHANT_ID + "\",\"body\":{\"uuid\":\"inv-1\"}}")));
    }

    @Test
    void merchantEnvelopeRequiresMerchantId(WireMockRuntimeInfo wm) {
        HttpTransport withoutMerchant = transport(wm.getHttpBaseUrl(), null);
        ApiRequest request = ApiRequest.builder(ApiRequest.Method.POST, "/api/v2/invoices")
                .body(Map.of("amount", 1)).merchantEnvelope().build();

        assertThatThrownBy(() -> withoutMerchant.execute(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("merchantId");
    }

    static Stream<Arguments> errorResponses() {
        return Stream.of(
                Arguments.of(200, "15000", ApiException.class),
                Arguments.of(200, "01000", RequestRejectedException.class),
                Arguments.of(400, "15400", ValidationException.class),
                Arguments.of(400, "15000", ValidationException.class),
                Arguments.of(401, "14010", AuthenticationException.class),
                Arguments.of(200, "14014", AuthenticationException.class),
                Arguments.of(402, "01200", InsufficientBalanceException.class),
                Arguments.of(402, "01300", PayoutLimitException.class),
                Arguments.of(402, "01400", PayoutLimitException.class),
                Arguments.of(429, "01500", PayoutLimitException.class),
                Arguments.of(418, "15000", ApiException.class),
                Arguments.of(500, "15000", ApiException.class),
                Arguments.of(503, "15000", ApiException.class));
    }

    @ParameterizedTest
    @MethodSource("errorResponses")
    void mapsFailuresToTypedExceptions(int status, String code, Class<? extends ApiException> expected) {
        stubFor(post("/api/v3/refund").willReturn(aResponse().withStatus(status)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"code\":\"" + code + "\",\"message\":\"failure-" + code + "\",\"responseId\":\"r-9\"}")));

        assertThatThrownBy(() -> transport.execute(
                ApiRequest.builder(ApiRequest.Method.POST, "/api/v3/refund").body(Map.of("orderId", "ORD-1")).build()))
                .isExactlyInstanceOf(expected)
                .hasMessage("failure-" + code)
                .extracting("httpStatus", "code", "responseId")
                .containsExactly(status, code, "r-9");
    }

    @Test
    void nonJsonErrorBodyKeepsStatus() {
        stubFor(get("/api/v3/orders/ORD-1").willReturn(aResponse().withStatus(502).withBody("<html>Bad Gateway</html>")));

        assertThatThrownBy(() -> transport.execute(ApiRequest.builder(ApiRequest.Method.GET, "/api/v3/orders/ORD-1").build()))
                .isExactlyInstanceOf(ApiException.class)
                .hasMessage("<html>Bad Gateway</html>")
                .extracting("httpStatus", "code")
                .containsExactly(502, null);
    }

    @Test
    void successStatusWithoutEnvelopeIsRejected() {
        stubFor(get("/api/v3/orders/ORD-1").willReturn(okJson("{\"orderId\":\"ORD-1\"}")));

        assertThatThrownBy(() -> transport.execute(ApiRequest.builder(ApiRequest.Method.GET, "/api/v3/orders/ORD-1").build()))
                .isExactlyInstanceOf(ApiException.class)
                .hasMessage("Unexpected response from Payriff");
    }

    @Test
    void downloadReturnsBytes() {
        byte[] pdf = {'%', 'P', 'D', 'F'};
        stubFor(get("/api/v3/payout/receipt/rrn-1").willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/pdf").withBody(pdf)));

        assertThat(transport.download(ApiRequest.builder(ApiRequest.Method.GET, "/api/v3/payout/receipt/rrn-1").build()))
                .containsExactly(pdf);
    }

    @Test
    void downloadMapsEnvelopeError() {
        stubFor(get("/api/v3/payout/receipt/rrn-1").willReturn(aResponse().withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"code\":\"15400\",\"message\":\"Not found\"}")));

        assertThatThrownBy(() -> transport.download(ApiRequest.builder(ApiRequest.Method.GET, "/api/v3/payout/receipt/rrn-1").build()))
                .isExactlyInstanceOf(ValidationException.class);
    }

    @Test
    void connectionFailureRaisesConnectionException() {
        HttpTransport unreachable = transport("http://127.0.0.1:1", MERCHANT_ID);

        assertThatThrownBy(() -> unreachable.execute(ApiRequest.builder(ApiRequest.Method.GET, "/api/v3/orders/ORD-1").build()))
                .isExactlyInstanceOf(PayriffConnectionException.class)
                .extracting("httpStatus")
                .isEqualTo(0);
    }

    private static HttpTransport transport(String baseUrl, String merchantId) {
        return new HttpTransport(URI.create(baseUrl), APP_KEY, merchantId, Duration.ofSeconds(5),
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build());
    }

    static final class Sample {
        public String orderId;
        public BigDecimal amount;
    }
}