package com.payriff.sdk;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.payriff.sdk.model.Currency;
import com.payriff.sdk.model.Invoice;
import com.payriff.sdk.model.InvoiceCreateRequest;
import com.payriff.sdk.model.InvoiceDetails;
import com.payriff.sdk.model.InvoiceStatus;
import com.payriff.sdk.model.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WireMockTest
class InvoicesApiTest {

    private PayriffClient client;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wm) {
        client = PayriffClient.builder().appKey("app-key").merchantId("ES1000000")
                .baseUrl(URI.create(wm.getHttpBaseUrl())).build();
    }

    @Test
    void createSendsMerchantEnvelope() {
        stubFor(post("/api/v2/invoices").willReturn(okJson("{\"code\":\"00000\",\"payload\":{"
                + "\"id\":9,\"invoiceUuid\":\"inv-uuid-1\",\"invoiceCode\":\"INV-001\",\"invoiceStatus\":\"PENDING\","
                + "\"paymentUrl\":\"https://pay.payriff.com/i/inv?type=preview\",\"amount\":15.00,\"totalAmount\":15.30,"
                + "\"currencyType\":\"AZN\",\"languageType\":\"AZ\",\"expireDate\":\"2026-10-08T23:59:00\"}}")));

        Invoice invoice = client.invoices().create(InvoiceCreateRequest.builder()
                .amount(new BigDecimal("15.00"))
                .language(Language.AZ)
                .fullName("JOHN DOE")
                .phoneNumber("+994501234567")
                .description("Consultation")
                .expireDate(LocalDateTime.of(2026, 10, 8, 23, 59))
                .sendSms(false)
                .metadata("bookingRef", "B-77")
                .build());

        verify(postRequestedFor(urlEqualTo("/api/v2/invoices"))
                .withRequestBody(equalToJson("{\"merchant\":\"ES1000000\",\"body\":{\"amount\":15.00,"
                        + "\"currencyType\":\"AZN\",\"languageType\":\"AZ\",\"fullName\":\"JOHN DOE\","
                        + "\"phoneNumber\":\"+994501234567\",\"description\":\"Consultation\","
                        + "\"expireDate\":\"2026-10-08T23:59:00\",\"sendSms\":false,\"metadata\":{\"bookingRef\":\"B-77\"}}}")));
        assertThat(invoice.getInvoiceUuid()).isEqualTo("inv-uuid-1");
        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PENDING);
        assertThat(invoice.getPaymentUrl()).endsWith("?type=preview");
        assertThat(invoice.getCurrency()).isEqualTo(Currency.AZN);
        assertThat(invoice.getExpireDate()).isEqualTo(LocalDateTime.of(2026, 10, 8, 23, 59));
    }

    @Test
    void amountRequiredUnlessDynamic() {
        assertThatThrownBy(() -> InvoiceCreateRequest.builder().build())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("amount");

        assertThat(InvoiceCreateRequest.builder().amountDynamic(true).build()).isNotNull();
    }

    @Test
    void getSendsInvoiceUuidInEnvelope() {
        stubFor(post("/api/v2/get-invoice").willReturn(okJson("{\"code\":\"00000\",\"payload\":{"
                + "\"invoiceUuid\":\"inv-uuid-1\",\"invoiceStatus\":\"COMPLETE\",\"amount\":15.00,"
                + "\"paymentDay\":\"2026-10-02\",\"createdDate\":\"2026-10-01T09:00:00.123\",\"metadata\":\"{}\"}}")));

        InvoiceDetails details = client.invoices().get("inv-uuid-1");

        verify(postRequestedFor(urlEqualTo("/api/v2/get-invoice"))
                .withRequestBody(equalToJson("{\"merchant\":\"ES1000000\",\"body\":{\"uuid\":\"inv-uuid-1\"}}")));
        assertThat(details.getStatus()).isEqualTo(InvoiceStatus.COMPLETE);
        assertThat(details.getPaymentDay()).isEqualTo(LocalDate.of(2026, 10, 2));
    }
}