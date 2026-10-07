package com.payriff.sdk;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.payriff.sdk.exception.InsufficientBalanceException;
import com.payriff.sdk.model.Page;
import com.payriff.sdk.model.PayoutFilter;
import com.payriff.sdk.model.PayoutRequest;
import com.payriff.sdk.model.PayoutResult;
import com.payriff.sdk.model.PayoutStatus;
import com.payriff.sdk.model.PayoutSummary;
import com.payriff.sdk.model.TransferState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URI;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WireMockTest
class PayoutsApiTest {

    private PayriffClient client;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wm) {
        client = PayriffClient.builder().appKey("app-key").merchantId("ES1000000")
                .baseUrl(URI.create(wm.getHttpBaseUrl())).build();
    }

    private static PayoutRequest.Builder payout() {
        return PayoutRequest.builder()
                .transferAmount(new BigDecimal("25.00"))
                .description("Refund to customer")
                .fullName("JOHN DOE")
                .finCode("1AB2C3D")
                .cardPan("4169741330151979")
                .requestRrn("po-1");
    }

    @Test
    void createWrapsBodyInMerchantEnvelope() {
        stubFor(post("/api/v3/payout").willReturn(okJson("{\"code\":\"00000\",\"payload\":{"
                + "\"_final\":\"true\",\"state\":\"SUCCESS\",\"currentDepositBalance\":975.00,\"walletHistoryId\":321,\"bankName\":\"KAPITAL\"}}")));

        PayoutResult result = client.payouts().create(payout().idempotencyKey("idem-po-1").build());

        verify(postRequestedFor(urlEqualTo("/api/v3/payout"))
                .withHeader("X-IDEMPOTENCY-KEY", equalTo("idem-po-1"))
                .withRequestBody(equalToJson("{\"merchant\":\"ES1000000\",\"body\":{\"transferAmount\":25.00,"
                        + "\"description\":\"Refund to customer\",\"fullName\":\"JOHN DOE\",\"finCode\":\"1AB2C3D\","
                        + "\"cardPan\":\"4169741330151979\",\"requestRrn\":\"po-1\"}}")));
        assertThat(result.getFinalState()).isEqualTo("true");
        assertThat(result.getState()).isEqualTo("SUCCESS");
        assertThat(result.getWalletHistoryId()).isEqualTo(321L);
        assertThat(result.getCurrentDepositBalance()).isEqualByComparingTo("975.00");
    }

    @Test
    void createMapsInsufficientBalance() {
        stubFor(post("/api/v3/payout").willReturn(aResponse().withStatus(402)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"code\":\"01200\",\"message\":\"Insufficient wallet balance\"}")));

        assertThatThrownBy(() -> client.payouts().create(payout().build()))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("Insufficient wallet balance");
    }

    @Test
    void amountBelowMinimumIsRejected() {
        assertThatThrownBy(() -> payout().transferAmount(new BigDecimal("0.99")).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("transferAmount must be at least 1");
    }

    @Test
    void toStringMasksCardPan() {
        assertThat(payout().build().toString()).contains("416974******1979").doesNotContain("4169741330151979");
    }

    @Test
    void getByRequestRrnParsesStatus() {
        stubFor(get("/api/v3/payout/info/po-1").willReturn(okJson("{\"code\":\"00000\",\"payload\":{"
                + "\"state\":\"IN_PROGRESS\",\"transferAmount\":25.00,\"bankName\":\"KAPITAL\","
                + "\"createdDate\":\"2026-10-01T10:00:00.000+00:00\",\"formattedDate\":\"01.10.2026 10:00:00\"}}")));

        PayoutStatus status = client.payouts().getByRequestRrn("po-1");

        assertThat(status.getState()).isEqualTo(TransferState.IN_PROGRESS);
        assertThat(status.getFormattedDate()).isEqualTo("01.10.2026 10:00:00");
    }

    @Test
    void checkCardholderNormalizesPan() {
        stubFor(post("/api/v3/payout/check-cardholder").willReturn(okJson("{\"code\":\"00000\",\"payload\":\"J*** D**\"}")));

        assertThat(client.payouts().checkCardholder("4169 7413 3015 1979")).isEqualTo("J*** D**");

        verify(postRequestedFor(urlEqualTo("/api/v3/payout/check-cardholder"))
                .withRequestBody(equalToJson("{\"cardPan\":\"4169741330151979\"}")));
    }

    @Test
    void checkCardholderRejectsShortPan() {
        assertThatThrownBy(() -> client.payouts().checkCardholder("4169"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("cardPan must be a 16-digit number");
    }

    @Test
    void listParsesPayoutPage() {
        stubFor(get(urlEqualTo("/api/v3/payouts?status=SUCCESS&page=0&offset=10")).willReturn(okJson("{\"code\":\"00000\",\"payload\":{"
                + "\"content\":[{\"id\":1,\"requestRrn\":\"po-1\",\"transferAmount\":25.00,\"fee\":0.25,"
                + "\"state\":\"SUCCESS\",\"createdDate\":\"2026-10-01T10:00:00\"}],\"totalElements\":1,\"totalPages\":1,"
                + "\"number\":0,\"size\":10,\"first\":true,\"last\":true}}")));

        Page<PayoutSummary> page = client.payouts().list(PayoutFilter.builder().status(TransferState.SUCCESS).build());

        assertThat(page.getContent()).extracting(PayoutSummary::getRequestRrn).containsExactly("po-1");
        assertThat(page.getContent().get(0).getState()).isEqualTo(TransferState.SUCCESS);
    }

    @Test
    void downloadReceiptReturnsPdf() {
        byte[] pdf = {'%', 'P', 'D', 'F', '-'};
        stubFor(get("/api/v3/payout/receipt/po-1").willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/pdf").withBody(pdf)));

        assertThat(client.payouts().downloadReceipt("po-1")).containsExactly(pdf);
    }
}