package com.payriff.sdk;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.payriff.sdk.model.CardSaveDetails;
import com.payriff.sdk.model.CardSaveRequest;
import com.payriff.sdk.model.CardSaveResponse;
import com.payriff.sdk.model.CardSaveStatus;
import com.payriff.sdk.model.Language;
import com.payriff.sdk.model.SavedCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
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

@WireMockTest
class CardsApiTest {

    private PayriffClient client;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wm) {
        client = PayriffClient.builder().appKey("app-key").baseUrl(URI.create(wm.getHttpBaseUrl())).build();
    }

    @Test
    void saveSendsBodyAndIdempotencyKey() {
        stubFor(post("/api/v3/cards/save").willReturn(okJson("{\"code\":\"00000\",\"payload\":{"
                + "\"cardSaveId\":\"0b6e1f6a-3c1d-4f5e-8a2b-9c7d6e5f4a3b\",\"orderId\":\"ORD-CS\","
                + "\"paymentUrl\":\"https://pay.payriff.com/ORD-CS\",\"amount\":0.10,\"currency\":\"AZN\",\"status\":\"CREATED\"}}")));

        CardSaveResponse response = client.cards().save(CardSaveRequest.builder()
                .customerRef("cust-1")
                .callbackUrl("https://shop.az/cards/cb")
                .language(Language.AZ)
                .idempotencyKey("idem-1")
                .build());

        verify(postRequestedFor(urlEqualTo("/api/v3/cards/save"))
                .withHeader("X-Idempotency-Key", equalTo("idem-1"))
                .withRequestBody(equalToJson("{\"customerRef\":\"cust-1\",\"callbackUrl\":\"https://shop.az/cards/cb\",\"language\":\"AZ\"}")));
        assertThat(response.getCardSaveId()).isEqualTo("0b6e1f6a-3c1d-4f5e-8a2b-9c7d6e5f4a3b");
        assertThat(response.getStatus()).isEqualTo(CardSaveStatus.CREATED);
        assertThat(response.getAmount()).isEqualByComparingTo("0.10");
    }

    @Test
    void getSaveParsesVerifiedCard() {
        stubFor(get("/api/v3/cards/save/cs-1").willReturn(okJson("{\"code\":\"00000\",\"payload\":{"
                + "\"cardSaveId\":\"cs-1\",\"status\":\"VERIFIED\",\"cardUuid\":\"card-1\",\"maskedPan\":\"416974******1979\","
                + "\"cardBrand\":\"VISA\",\"customerRef\":\"cust-1\",\"createdDate\":\"2026-10-01T10:00:00\","
                + "\"verifiedDate\":\"2026-10-01T10:01:30.5\"}}")));

        CardSaveDetails details = client.cards().getSave("cs-1");

        assertThat(details.getStatus()).isEqualTo(CardSaveStatus.VERIFIED);
        assertThat(details.getCardUuid()).isEqualTo("card-1");
        assertThat(details.getVerifiedDate()).isNotNull();
    }

    @Test
    void listReturnsCardsForCustomer() {
        stubFor(get("/api/v3/cards/save?customerRef=cust%201").willReturn(okJson("{\"code\":\"00000\",\"payload\":["
                + "{\"cardUuid\":\"card-1\",\"maskedPan\":\"416974******1979\",\"cardBrand\":\"VISA\"},"
                + "{\"cardUuid\":\"card-2\",\"maskedPan\":\"540000******0001\",\"cardBrand\":\"MASTERCARD\"}]}")));

        List<SavedCard> cards = client.cards().list("cust 1");

        assertThat(cards).extracting(SavedCard::getCardUuid).containsExactly("card-1", "card-2");
    }

    @Test
    void deleteCallsCardEndpoint() {
        stubFor(delete("/api/v3/cards/card-1").willReturn(okJson("{\"code\":\"00000\",\"payload\":true}")));

        client.cards().delete("card-1");

        verify(deleteRequestedFor(urlEqualTo("/api/v3/cards/card-1")));
    }
}