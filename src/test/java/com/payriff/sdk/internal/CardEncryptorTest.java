package com.payriff.sdk.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.payriff.sdk.model.CardData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.util.Arrays;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class CardEncryptorTest {

    private static KeyPair keyPair;

    private final CardData card = CardData.builder()
            .pan("4169741330151979")
            .cardHolder("JOHN DOE")
            .expiryYear("2027")
            .expiryMonth("11")
            .cvv("123")
            .build();

    @BeforeAll
    static void generateKeys() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.generateKeyPair();
    }

    @Test
    void encryptedCardDecryptsWithPayriffScheme() throws Exception {
        CardEncryptor.EncryptedCard encrypted = new CardEncryptor(keyPair.getPublic()).encrypt(card);

        Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPPadding");
        rsa.init(Cipher.DECRYPT_MODE, keyPair.getPrivate(),
                new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
        byte[] keyAndIv = rsa.doFinal(Base64.getDecoder().decode(encrypted.secretKey()));
        assertThat(keyAndIv).hasSize(44);

        Cipher aes = Cipher.getInstance("AES/GCM/NoPadding");
        aes.init(Cipher.DECRYPT_MODE,
                new SecretKeySpec(Arrays.copyOfRange(keyAndIv, 0, 32), "AES"),
                new GCMParameterSpec(128, Arrays.copyOfRange(keyAndIv, 32, 44)));
        JsonNode json = Json.mapper().readTree(aes.doFinal(Base64.getDecoder().decode(encrypted.encryptedMessage())));

        assertThat(json.get("pan").asText()).isEqualTo("4169741330151979");
        assertThat(json.get("cardHolder").asText()).isEqualTo("JOHN DOE");
        assertThat(json.get("expiryYear").asText()).isEqualTo("2027");
        assertThat(json.get("expiryMonth").asText()).isEqualTo("11");
        assertThat(json.get("cvv").asText()).isEqualTo("123");
        assertThat(json.size()).isEqualTo(5);
    }

    @Test
    void everyCallUsesFreshKeyMaterial() {
        CardEncryptor encryptor = new CardEncryptor(keyPair.getPublic());

        CardEncryptor.EncryptedCard first = encryptor.encrypt(card);
        CardEncryptor.EncryptedCard second = encryptor.encrypt(card);

        assertThat(first.encryptedMessage()).isNotEqualTo(second.encryptedMessage());
        assertThat(first.secretKey()).isNotEqualTo(second.secretKey());
    }

    @Test
    void productionKeyIsRsa2048() {
        RSAPublicKey key = (RSAPublicKey) PayriffKeys.productionCardEncryptionKey();

        assertThat(key.getModulus().bitLength()).isEqualTo(2048);
        assertThat(Base64.getDecoder().decode(new CardEncryptor(key).encrypt(card).secretKey())).hasSize(256);
    }

    @Test
    void pemAndBareBase64ParseToSameKey() {
        String pem = "-----BEGIN PUBLIC KEY-----\n" + PayriffKeys.PRODUCTION_CARD_ENCRYPTION_KEY + "\n-----END PUBLIC KEY-----\n";

        assertThat(PayriffKeys.parse(pem)).isEqualTo(PayriffKeys.productionCardEncryptionKey());
    }
}