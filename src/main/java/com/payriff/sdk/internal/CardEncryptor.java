package com.payriff.sdk.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payriff.sdk.model.CardData;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.util.Arrays;
import java.util.Base64;

public final class CardEncryptor {

    static final int AES_KEY_BYTES = 32;
    static final int IV_BYTES = 12;
    static final int GCM_TAG_BITS = 128;

    private static final OAEPParameterSpec OAEP_SHA256 = new OAEPParameterSpec(
            "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);

    private final PublicKey publicKey;
    private final SecureRandom random;

    public CardEncryptor(PublicKey publicKey) {
        this(publicKey, new SecureRandom());
    }

    CardEncryptor(PublicKey publicKey, SecureRandom random) {
        this.publicKey = publicKey;
        this.random = random;
    }

    public EncryptedCard encrypt(CardData card) {
        byte[] aesKey = new byte[AES_KEY_BYTES];
        byte[] iv = new byte[IV_BYTES];
        random.nextBytes(aesKey);
        random.nextBytes(iv);
        byte[] keyAndIv = new byte[AES_KEY_BYTES + IV_BYTES];
        byte[] plaintext = null;
        try {
            plaintext = Json.mapper().writeValueAsBytes(card);

            Cipher aes = Cipher.getInstance("AES/GCM/NoPadding");
            aes.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] ciphertext = aes.doFinal(plaintext);

            System.arraycopy(aesKey, 0, keyAndIv, 0, AES_KEY_BYTES);
            System.arraycopy(iv, 0, keyAndIv, AES_KEY_BYTES, IV_BYTES);
            Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPPadding");
            rsa.init(Cipher.ENCRYPT_MODE, publicKey, OAEP_SHA256);
            byte[] secret = rsa.doFinal(keyAndIv);

            Base64.Encoder b64 = Base64.getEncoder();
            return new EncryptedCard(b64.encodeToString(ciphertext), b64.encodeToString(secret));
        } catch (JsonProcessingException | GeneralSecurityException e) {
            throw new IllegalStateException("Card encryption failed", e);
        } finally {
            Arrays.fill(aesKey, (byte) 0);
            Arrays.fill(iv, (byte) 0);
            Arrays.fill(keyAndIv, (byte) 0);
            if (plaintext != null) {
                Arrays.fill(plaintext, (byte) 0);
            }
        }
    }

    public static final class EncryptedCard {
        private final String encryptedMessage;
        private final String secretKey;

        EncryptedCard(String encryptedMessage, String secretKey) {
            this.encryptedMessage = encryptedMessage;
            this.secretKey = secretKey;
        }

        public String encryptedMessage() {
            return encryptedMessage;
        }

        public String secretKey() {
            return secretKey;
        }
    }
}