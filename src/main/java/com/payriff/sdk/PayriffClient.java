package com.payriff.sdk;

import com.payriff.sdk.internal.CardEncryptor;
import com.payriff.sdk.internal.HttpTransport;
import com.payriff.sdk.internal.PayriffKeys;

import java.net.URI;
import java.net.http.HttpClient;
import java.security.PublicKey;
import java.time.Duration;
import java.util.Objects;

public final class PayriffClient {

    static final URI PRODUCTION_URL = URI.create("https://api.payriff.com");

    private final HttpTransport transport;
    private final CardEncryptor cardEncryptor;
    private final OrdersApi orders;
    private final PaymentsApi payments;
    private final CardsApi cards;
    private final TransactionsApi transactions;
    private final PayoutsApi payouts;
    private final InvoicesApi invoices;

    private PayriffClient(Builder b) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(b.connectTimeout)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
        this.transport = new HttpTransport(b.baseUrl, b.appKey, b.merchantId, b.requestTimeout, httpClient);
        this.cardEncryptor = new CardEncryptor(b.cardEncryptionKey);
        this.orders = new OrdersApi(transport);
        this.payments = new PaymentsApi(transport, cardEncryptor);
        this.cards = new CardsApi(transport);
        this.transactions = new TransactionsApi(transport);
        this.payouts = new PayoutsApi(transport);
        this.invoices = new InvoicesApi(transport);
    }

    public static Builder builder() {
        return new Builder();
    }

    public OrdersApi orders() {
        return orders;
    }

    public PaymentsApi payments() {
        return payments;
    }

    public CardsApi cards() {
        return cards;
    }

    public TransactionsApi transactions() {
        return transactions;
    }

    public PayoutsApi payouts() {
        return payouts;
    }

    public InvoicesApi invoices() {
        return invoices;
    }

    HttpTransport transport() {
        return transport;
    }

    CardEncryptor cardEncryptor() {
        return cardEncryptor;
    }

    public static final class Builder {
        private String appKey;
        private String merchantId;
        private Duration connectTimeout = Duration.ofSeconds(10);
        private Duration requestTimeout = Duration.ofSeconds(60);
        private URI baseUrl = PRODUCTION_URL;
        private PublicKey cardEncryptionKey;

        private Builder() {
        }

        public Builder appKey(String appKey) {
            this.appKey = appKey;
            return this;
        }

        public Builder merchantId(String merchantId) {
            this.merchantId = merchantId;
            return this;
        }

        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = Objects.requireNonNull(connectTimeout, "connectTimeout");
            return this;
        }

        public Builder requestTimeout(Duration requestTimeout) {
            this.requestTimeout = Objects.requireNonNull(requestTimeout, "requestTimeout");
            return this;
        }

        public Builder cardEncryptionKey(String base64OrPem) {
            this.cardEncryptionKey = PayriffKeys.parse(Objects.requireNonNull(base64OrPem, "cardEncryptionKey"));
            return this;
        }

        Builder baseUrl(URI baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public PayriffClient build() {
            if (appKey == null || appKey.trim().isEmpty()) {
                throw new IllegalStateException("appKey is required");
            }
            if (cardEncryptionKey == null) {
                cardEncryptionKey = PayriffKeys.productionCardEncryptionKey();
            }
            return new PayriffClient(this);
        }
    }
}