package com.payriff.sdk.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CardDataTest {

    @ParameterizedTest
    @CsvSource({
            "4169 7413 3015 1979, 1,  27,   4169741330151979, 01, 2027",
            "4169-7413-3015-1979, 09, 2030, 4169741330151979, 09, 2030",
            "4169741330151979,    12, 2027, 4169741330151979, 12, 2027"
    })
    void normalizesInput(String pan, String month, String year,
                         String expectedPan, String expectedMonth, String expectedYear) {
        CardData card = CardData.builder().pan(pan).cardHolder(" JOHN DOE ")
                .expiryMonth(month).expiryYear(year).cvv("123").build();

        assertThat(card.getPan()).isEqualTo(expectedPan);
        assertThat(card.getExpiryMonth()).isEqualTo(expectedMonth);
        assertThat(card.getExpiryYear()).isEqualTo(expectedYear);
        assertThat(card.getCardHolder()).isEqualTo("JOHN DOE");
    }

    @ParameterizedTest
    @CsvSource({
            "41697413301,      11, 2027, 123,   pan must contain 12-19 digits",
            "4169abcd30151979, 11, 2027, 123,   pan must contain digits only",
            "4169741330151979, 13, 2027, 123,   expiryMonth must be 1-12",
            "4169741330151979, 11, 202,  123,   expiryYear must have 2 or 4 digits",
            "4169741330151979, 11, 2027, 12,    cvv must contain 3-4 digits"
    })
    void rejectsInvalidInput(String pan, String month, String year, String cvv, String message) {
        CardData.Builder builder = CardData.builder().pan(pan).cardHolder("JOHN DOE")
                .expiryMonth(month).expiryYear(year).cvv(cvv);

        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void toStringMasksSensitiveData() {
        CardData card = CardData.builder().pan("4169741330151979").cardHolder("JOHN DOE")
                .expiryMonth("11").expiryYear("2027").cvv("123").build();

        assertThat(card.toString())
                .isEqualTo("CardData{pan=416974******1979, expiry=11/2027}")
                .doesNotContain("123");
    }
}