package com.geofleet.rider.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        Instant timestamp,
        String path,
        int status,
        String error,
        String message,
        String traceId,
        Map<String, String> fieldErrors
) {
}

