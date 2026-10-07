package com.payriff.sdk;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PayriffClientTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void appKeyIsRequired(String appKey) {
        assertThatThrownBy(() -> PayriffClient.builder().appKey(appKey).build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("appKey is required");
    }

    @Test
    void defaultsToProduction() {
        assertThat(PayriffClient.PRODUCTION_URL.toString()).isEqualTo("https://api.payriff.com");
        assertThat(PayriffClient.builder().appKey("key").build().cardEncryptor()).isNotNull();
    }

    @Test
    void rejectsInvalidEncryptionKey() {
        assertThatThrownBy(() -> PayriffClient.builder().appKey("key").cardEncryptionKey("not-a-key"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid RSA public key");
    }
}