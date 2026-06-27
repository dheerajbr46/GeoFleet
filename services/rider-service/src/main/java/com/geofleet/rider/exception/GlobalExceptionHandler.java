package com.geofleet.rider.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        ApiErrorResponse body = baseError(
                HttpStatus.BAD_REQUEST,
                "Request validation failed. Check fieldErrors for details.",
                request,
                fieldErrors
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = extractJsonFieldErrors(ex);
        String message = fieldErrors.isEmpty()
                ? "Request body is malformed or contains invalid JSON."
                : "Request body contains invalid field values. Check fieldErrors for details.";

        ApiErrorResponse body = baseError(HttpStatus.BAD_REQUEST, message, request, fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    private ResponseEntity<ApiErrorResponse> error(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(status).body(baseError(status, message, request, null));
    }

    private ApiErrorResponse baseError(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> fieldErrors
    ) {
        return new ApiErrorResponse(
                Instant.now(),
                request.getRequestURI(),
                status.value(),
                status.getReasonPhrase(),
                message,
                traceId(request),
                fieldErrors == null || fieldErrors.isEmpty() ? null : fieldErrors
        );
    }

    private Map<String, String> extractJsonFieldErrors(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            String fieldName = fieldName(invalidFormatException.getPath());
            Class<?> targetType = invalidFormatException.getTargetType();

            if (targetType != null && targetType.isEnum()) {
                return Map.of(
                        fieldName,
                        "Invalid value '%s'. Accepted values: %s"
                                .formatted(invalidFormatException.getValue(), enumValues(targetType))
                );
            }

            return Map.of(
                    fieldName,
                    "Invalid value '%s' for expected type %s"
                            .formatted(invalidFormatException.getValue(), targetType.getSimpleName())
            );
        }

        return Map.of();
    }

    private String fieldName(List<JsonMappingException.Reference> path) {
        if (path == null || path.isEmpty()) {
            return "requestBody";
        }

        JsonMappingException.Reference lastReference = path.getLast();
        String fieldName = lastReference.getFieldName();
        return fieldName == null ? "requestBody" : fieldName;
    }

    private String enumValues(Class<?> enumType) {
        return Arrays.stream(enumType.getFields())
                .filter(Field::isEnumConstant)
                .map(Field::getName)
                .toList()
                .toString();
    }

    private String traceId(HttpServletRequest request) {
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isBlank()) {
            traceId = MDC.get("traceId");
        }
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }
        return traceId;
    }
}
