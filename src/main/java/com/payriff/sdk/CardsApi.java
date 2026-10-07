package com.payriff.sdk;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.payriff.sdk.internal.ApiRequest;
import com.payriff.sdk.internal.HttpTransport;
import com.payriff.sdk.model.CardSaveDetails;
import com.payriff.sdk.model.CardSaveRequest;
import com.payriff.sdk.model.CardSaveResponse;
import com.payriff.sdk.model.SavedCard;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.payriff.sdk.internal.ApiRequest.Method.DELETE;
import static com.payriff.sdk.internal.ApiRequest.Method.GET;
import static com.payriff.sdk.internal.ApiRequest.Method.POST;
import static com.payriff.sdk.internal.PathSegment.encode;

public final class CardsApi {

    private final HttpTransport transport;

    CardsApi(HttpTransport transport) {
        this.transport = transport;
    }

    public CardSaveResponse save(CardSaveRequest request) {
        Objects.requireNonNull(request, "request");
        return transport.execute(ApiRequest.builder(POST, "/api/v3/cards/save")
                .header("X-Idempotency-Key", request.getIdempotencyKey())
                .body(request)
                .payload(CardSaveResponse.class)
                .build());
    }

    public CardSaveDetails getSave(String cardSaveId) {
        return transport.execute(ApiRequest.builder(GET, "/api/v3/cards/save/" + encode(cardSaveId, "cardSaveId"))
                .payload(CardSaveDetails.class)
                .build());
    }

    public List<SavedCard> list(String customerRef) {
        encode(customerRef, "customerRef");
        List<SavedCard> cards = transport.execute(ApiRequest.builder(GET, "/api/v3/cards/save")
                .query("customerRef", customerRef)
                .payload(TypeFactory.defaultInstance().constructCollectionType(List.class, SavedCard.class))
                .build());
        return cards == null ? Collections.emptyList() : Collections.unmodifiableList(cards);
    }

    public void delete(String cardUuid) {
        transport.execute(ApiRequest.builder(DELETE, "/api/v3/cards/" + encode(cardUuid, "cardUuid")).build());
    }
}