package com.payriff.sdk;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.payriff.sdk.model.Page;
import com.payriff.sdk.model.PaymentStatus;
import com.payriff.sdk.model.Transaction;
import com.payriff.sdk.model.TransactionFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;
import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WireMockTest
class TransactionsApiTest {

    private PayriffClient client;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wm) {
        client = PayriffClient.builder().appKey("app-key").baseUrl(URI.create(wm.getHttpBaseUrl())).build();
    }

    @Test
    void listSendsFilterAndParsesPage() {
        stubFor(get(urlEqualTo("/api/v3/transactions?status=APPROVED&from=01.09.2026&to=30.09.2026&page=1&offset=20"))
                .willReturn(okJson("{\"code\":\"00000\",\"payload\":{\"content\":[{"
                        + "\"id\":5,\"orderId\":\"ORD-1\",\"amount\":10.00,\"currencyType\":\"AZN\",\"paymentStatus\":\"APPROVED\","
                        + "\"card_brand\":\"VISA\",\"payment_way\":\"DIRECT\",\"extra_payment\":0.50,"
                        + "\"createdDate\":\"2026-09-15 10:00:00\"}],"
                        + "\"totalElements\":41,\"totalPages\":3,\"number\":1,\"size\":20,\"first\":false,\"last\":false,"
                        + "\"pageable\":{\"pageNumber\":1},\"sort\":{\"sorted\":false}}}")));

        Page<Transaction> page = client.transactions().list(TransactionFilter.builder()
                .status(PaymentStatus.APPROVED)
                .from(LocalDate.of(2026, 9, 1))
                .to(LocalDate.of(2026, 9, 30))
                .page(1)
                .size(20)
                .build());

        assertThat(page.getTotalElements()).isEqualTo(41);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.isLast()).isFalse();
        Transaction tx = page.getContent().get(0);
        assertThat(tx.getOrderId()).isEqualTo("ORD-1");
        assertThat(tx.getCardBrand()).isEqualTo("VISA");
        assertThat(tx.getPaymentWay()).isEqualTo("DIRECT");
        assertThat(tx.getExtraPayment()).isEqualByComparingTo("0.50");
        assertThat(tx.getPaymentStatus()).isEqualTo(PaymentStatus.APPROVED);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 21, -1})
    void sizeMustBeWithinServerCap(int size) {
        assertThatThrownBy(() -> TransactionFilter.builder().size(size))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("size must be 1-20");
    }

    @Test
    void defaultFilterRequestsFirstPage() {
        assertThat(TransactionFilter.builder().build().toQuery())
                .containsEntry("page", "0")
                .containsEntry("offset", "10")
                .hasSize(2);
    }
}