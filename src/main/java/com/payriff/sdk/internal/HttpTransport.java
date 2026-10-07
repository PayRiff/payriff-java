package com.payriff.sdk.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.payriff.sdk.exception.ApiException;
import com.payriff.sdk.exception.PayriffConnectionException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.StringJoiner;

public final class HttpTransport {

    private static final String USER_AGENT = "payriff-java/" + sdkVersion();
    private static final int MAX_ERROR_BODY = 500;

    private final URI baseUrl;
    private final String appKey;
    private final String merchantId;
    private final Duration requestTimeout;
    private final HttpClient httpClient;
    private final ObjectMapper mapper = Json.mapper();

    public HttpTransport(URI baseUrl, String appKey, String merchantId, Duration requestTimeout, HttpClient httpClient) {
        this.baseUrl = baseUrl;
        this.appKey = appKey;
        this.merchantId = merchantId;
        this.requestTimeout = requestTimeout;
        this.httpClient = httpClient;
    }

    public <T> T execute(ApiRequest request) {
        HttpResponse<String> response = send(request, "application/json", HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return unwrap(request, response.statusCode(), response.body());
    }

    public byte[] download(ApiRequest request) {
        HttpResponse<byte[]> response = send(request, "application/pdf, application/json", HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() / 100 == 2) {
            return response.body();
        }
        unwrap(request, response.statusCode(), new String(response.body(), StandardCharsets.UTF_8));
        throw new ApiException("Unexpected response", response.statusCode(), null, null);
    }

    private <B> HttpResponse<B> send(ApiRequest request, String accept, HttpResponse.BodyHandler<B> handler) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri(request))
                .timeout(requestTimeout)
                .header("Accept", accept)
                .header("User-Agent", USER_AGENT);
        if (request.authenticated()) {
            builder.header("Authorization", appKey);
        }
        request.headers().forEach(builder::header);

        String json = requestBody(request);
        if (json != null) {
            builder.header("Content-Type", "application/json");
            builder.method(request.method().name(), HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));
        } else {
            builder.method(request.method().name(), HttpRequest.BodyPublishers.noBody());
        }

        try {
            return httpClient.send(builder.build(), handler);
        } catch (IOException e) {
            throw new PayriffConnectionException("Payriff request failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PayriffConnectionException("Payriff request interrupted", e);
        }
    }

    private URI uri(ApiRequest request) {
        String base = baseUrl.toString().replaceAll("/+$", "");
        StringBuilder sb = new StringBuilder(base).append(request.path());
        if (!request.query().isEmpty()) {
            StringJoiner joiner = new StringJoiner("&", "?", "");
            for (Map.Entry<String, String> e : request.query().entrySet()) {
                joiner.add(encode(e.getKey()) + "=" + encode(e.getValue()));
            }
            sb.append(joiner);
        }
        return URI.create(sb.toString());
    }

    private String requestBody(ApiRequest request) {
        if (request.body() == null && !request.merchantEnvelope()) {
            return null;
        }
        try {
            if (request.merchantEnvelope()) {
                if (merchantId == null || merchantId.isEmpty()) {
                    throw new IllegalStateException("merchantId must be configured on PayriffClient for this operation");
                }
                ObjectNode envelope = mapper.createObjectNode();
                envelope.put("merchant", merchantId);
                envelope.set("body", mapper.valueToTree(request.body()));
                return mapper.writeValueAsString(envelope);
            }
            return mapper.writeValueAsString(request.body());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Request body cannot be serialized", e);
        }
    }

    private <T> T unwrap(ApiRequest request, int status, String body) {
        JsonNode root;
        try {
            root = body == null || body.isEmpty() ? null : mapper.readTree(body);
        } catch (JsonProcessingException e) {
            root = null;
        }

        if (root == null || !root.isObject() || !root.has("code")) {
            throw new ApiException(status / 100 == 2 ? "Unexpected response from Payriff" : truncate(body), status, null, null);
        }

        String code = root.path("code").asText(null);
        String message = root.path("message").asText(null);
        String responseId = root.path("responseId").asText(null);

        if (status / 100 != 2 || !ResultCodes.SUCCESS.equals(code)) {
            throw ResultCodes.toException(message, status, code, responseId);
        }

        JsonNode payload = root.get("payload");
        if (request.payloadType().getRawClass() == Void.class || payload == null || payload.isNull()) {
            return null;
        }
        try {
            return mapper.readerFor(request.payloadType()).readValue(payload);
        } catch (IOException e) {
            throw new ApiException("Cannot parse Payriff response: " + e.getMessage(), status, code, responseId);
        }
    }

    private static String truncate(String body) {
        if (body == null || body.isEmpty()) {
            return null;
        }
        return body.length() > MAX_ERROR_BODY ? body.substring(0, MAX_ERROR_BODY) + "..." : body;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static String sdkVersion() {
        String version = HttpTransport.class.getPackage().getImplementationVersion();
        return version != null ? version : "dev";
    }
}