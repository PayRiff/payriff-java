package com.payriff.sdk.internal;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ApiRequest {

    public enum Method { GET, POST, PATCH, DELETE }

    private final Method method;
    private final String path;
    private final Map<String, String> query;
    private final Map<String, String> headers;
    private final Object body;
    private final boolean merchantEnvelope;
    private final boolean authenticated;
    private final JavaType payloadType;

    private ApiRequest(Builder b) {
        this.method = b.method;
        this.path = b.path;
        this.query = Collections.unmodifiableMap(new LinkedHashMap<>(b.query));
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(b.headers));
        this.body = b.body;
        this.merchantEnvelope = b.merchantEnvelope;
        this.authenticated = b.authenticated;
        this.payloadType = b.payloadType;
    }

    public static Builder builder(Method method, String path) {
        return new Builder(method, path);
    }

    public Method method() {
        return method;
    }

    public String path() {
        return path;
    }

    public Map<String, String> query() {
        return query;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public Object body() {
        return body;
    }

    public boolean merchantEnvelope() {
        return merchantEnvelope;
    }

    public boolean authenticated() {
        return authenticated;
    }

    public JavaType payloadType() {
        return payloadType;
    }

    public static final class Builder {
        private final Method method;
        private final String path;
        private final Map<String, String> query = new LinkedHashMap<>();
        private final Map<String, String> headers = new LinkedHashMap<>();
        private Object body;
        private boolean merchantEnvelope;
        private boolean authenticated = true;
        private JavaType payloadType = TypeFactory.defaultInstance().constructType(Void.class);

        private Builder(Method method, String path) {
            this.method = method;
            this.path = path;
        }

        public Builder query(String name, Object value) {
            if (value != null) {
                query.put(name, String.valueOf(value));
            }
            return this;
        }

        public Builder query(Map<String, String> values) {
            values.forEach(this::query);
            return this;
        }

        public Builder header(String name, String value) {
            if (value != null) {
                headers.put(name, value);
            }
            return this;
        }

        public Builder body(Object body) {
            this.body = body;
            return this;
        }

        public Builder merchantEnvelope() {
            this.merchantEnvelope = true;
            return this;
        }

        public Builder unauthenticated() {
            this.authenticated = false;
            return this;
        }

        public Builder payload(Class<?> type) {
            this.payloadType = TypeFactory.defaultInstance().constructType(type);
            return this;
        }

        public Builder payload(JavaType type) {
            this.payloadType = type;
            return this;
        }

        public ApiRequest build() {
            return new ApiRequest(this);
        }
    }
}