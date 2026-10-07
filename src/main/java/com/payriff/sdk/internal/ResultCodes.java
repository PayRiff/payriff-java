package com.payriff.sdk.internal;

import com.payriff.sdk.exception.ApiException;
import com.payriff.sdk.exception.AuthenticationException;
import com.payriff.sdk.exception.InsufficientBalanceException;
import com.payriff.sdk.exception.PayoutLimitException;
import com.payriff.sdk.exception.RequestRejectedException;
import com.payriff.sdk.exception.ValidationException;

import java.util.Set;

public final class ResultCodes {

    public static final String SUCCESS = "00000";

    private static final Set<String> AUTH = Set.of("14010", "14013", "14014", "14015");
    private static final Set<String> PAYOUT_LIMIT = Set.of("01300", "01400", "01500");

    private ResultCodes() {
    }

    public static ApiException toException(String message, int httpStatus, String code, String responseId) {
        String msg = message != null && !message.isEmpty() ? message : "Payriff request failed (HTTP " + httpStatus + ")";
        if (code != null) {
            if (AUTH.contains(code)) {
                return new AuthenticationException(msg, httpStatus, code, responseId);
            }
            if (PAYOUT_LIMIT.contains(code)) {
                return new PayoutLimitException(msg, httpStatus, code, responseId);
            }
            switch (code) {
                case "01200":
                    return new InsufficientBalanceException(msg, httpStatus, code, responseId);
                case "01000":
                    return new RequestRejectedException(msg, httpStatus, code, responseId);
                case "15400":
                    return new ValidationException(msg, httpStatus, code, responseId);
                default:
                    break;
            }
        }
        if (httpStatus == 401 || httpStatus == 403) {
            return new AuthenticationException(msg, httpStatus, code, responseId);
        }
        if (httpStatus == 400) {
            return new ValidationException(msg, httpStatus, code, responseId);
        }
        return new ApiException(msg, httpStatus, code, responseId);
    }
}