package com.payriff.sdk;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.payriff.sdk.internal.Json;
import com.payriff.sdk.model.AutoPayRequest;
import com.payriff.sdk.model.AutoPayResponse;
import com.payriff.sdk.model.CardData;
import com.payriff.sdk.model.DirectPayRequest;
import com.payriff.sdk.model.DirectPayResponse;
import com.payriff.sdk.model.PaymentStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.MGF1ParameterSpec;
import java.util.Arrays;
import java.util.Base64;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
class PaymentsApiTest {

    private static KeyPair keyPair;

    private PayriffClient client;

    @BeforeAll
    static void generateKeys() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.generateKeyPair();
    }

    @BeforeEach
    void setUp(WireMockRuntimeInfo wm) {
        client = PayriffClient.builder()
                .appKey("app-key")
                .cardEncryptionKey(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()))
                .baseUrl(URI.create(wm.getHttpBaseUrl()))
                .build();
    }

    @Test
    void directPayEncryptsCardAndSendsSecretKey() throws Exception {
        stubFor(post("/api/v3/directPay").willReturn(okJson("{\"code\":\"00000\",\"payload\":{"
                + "\"orderId\":\"ORD-1\",\"threeDS\":true,\"redirect\":true,\"redirectUrl\":\"https://acs.bank/3ds\","
                + "\"transactionResponse\":{\"status\":\"CREATED\",\"requestRrn\":\"req-1\"}}}")));

        DirectPayResponse response = client.payments().directPay(DirectPayRequest.builder()
                .amount(new BigDecimal("1.00"))
                .description("Order #1")
                .callbackUrl("https://shop.az/cb")
                .cardSave(true)
                .requestRrn("rrn-1")
                .card(CardData.builder().pan("4169741330151979").cardHolder("JOHN DOE")
                        .expiryMonth("11").expiryYear("2027").cvv("123").build())
                .build());

        LoggedRequest sent = findAll(postRequestedFor(urlEqualTo("/api/v3/directPay"))).get(0);
        assertThat(sent.getHeader("Authorization")).isEqualTo("app-key");
        assertThat(sent.getHeader("X-REQUEST-RRN")).isEqualTo("rrn-1");

        JsonNode body = Json.mapper().readTree(sent.getBodyAsString());
        assertThat(body.get("amount").decimalValue()).isEqualByComparingTo("1.00");
        assertThat(body.get("currency").asText()).isEqualTo("AZN");
        assertThat(body.get("operation").asText()).isEqualTo("PURCHASE");
        assertThat(body.get("description").asText()).isEqualTo("Order #1");
        assertThat(body.has("card")).isFalse();
        assertThat(body.has("requestRrn")).isFalse();
        assertThat(body.has("cardSave")).isFalse();
        assertThat(sent.getBodyAsString()).doesNotContain("4169741330151979").doesNotContain("JOHN DOE");

        JsonNode paymentData = body.get("paymentData");
        assertThat(paymentData.get("paymentWay").asText()).isEqualTo("DIRECT");
        assertThat(paymentData.get("cardSave").asBoolean()).isTrue();

        JsonNode card = decrypt(sent.getHeader("x-secret-key"), paymentData.get("encryptedMessage").asText());
        assertThat(card.get("pan").asText()).isEqualTo("4169741330151979");
        assertThat(card.get("cvv").asText()).isEqualTo("123");

        assertThat(response.getOrderId()).isEqualTo("ORD-1");
        assertThat(response.isRedirect()).isTrue();
        assertThat(response.getRedirectUrl()).isEqualTo("https://acs.bank/3ds");
        assertThat(response.getTransaction().getStatus()).isEqualTo("CREATED");
    }

    @Test
    void autoPaySendsExplicitCurrencyAndOneClickFlag() {
        stubFor(post("/api/v3/autoPay").willReturn(okJson("{\"code\":\"00000\",\"payload\":{"
                + "\"orderId\":\"ORD-2\",\"amount\":3.00,\"paymentStatus\":\"APPROVED\",\"auto\":true,"
                + "\"createdDate\":\"2026-10-01T10:00:00.000000\",\"transactionResponseDto\":{\"threeDS\":false}}}")));

        AutoPayResponse response = client.payments().autoPay(AutoPayRequest.builder()
                .cardUuid("card-uuid-1")
                .amount(new BigDecimal("3.00"))
                .description("Subscription")
                .oneClickPayment(true)
                .requestRrn("rrn-2")
                .build());

        verify(postRequestedFor(urlEqualTo("/api/v3/autoPay"))
                .withHeader("X-REQUEST-RRN", equalTo("rrn-2"))
                .withRequestBody(equalToJson("{\"cardUuid\":\"card-uuid-1\",\"amount\":3.00,\"operation\":\"PURCHASE\","
                        + "\"currency\":\"AZN\",\"description\":\"Subscription\",\"isOneCLickPayment\":true}")));
        assertThat(response.getOrderId()).isEqualTo("ORD-2");
        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(response.isAuto()).isTrue();
        assertThat(response.getTransactionResponse().isThreeDS()).isFalse();
    }

    private static JsonNode decrypt(String secretKey, String encryptedMessage) throws Exception {
        Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPPadding");
        rsa.init(Cipher.DECRYPT_MODE, keyPair.getPrivate(),
                new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
        byte[] keyAndIv = rsa.doFinal(Base64.getDecoder().decode(secretKey));

        Cipher aes = Cipher.getInstance("AES/GCM/NoPadding");
        aes.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Arrays.copyOfRange(keyAndIv, 0, 32), "AES"),
                new GCMParameterSpec(128, Arrays.copyOfRange(keyAndIv, 32, 44)));
        return Json.mapper().readTree(aes.doFinal(Base64.getDecoder().decode(encryptedMessage)));
    }
}