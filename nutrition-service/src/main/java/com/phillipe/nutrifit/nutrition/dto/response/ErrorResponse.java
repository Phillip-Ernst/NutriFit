package com.phillipe.nutrifit.nutrition.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private String error;
    private String message;
    private Instant timestamp;
    private List<FieldError> fieldErrors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldError {
        private String field;
        private String message;
    }

    public static ErrorResponse of(String error, String message) {
        return ErrorResponse.builder()
                .error(error)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static ErrorResponse withFieldErrors(String error, String message, List<FieldError> fieldErrors) {
        return ErrorResponse.builder()
                .error(error)
                .message(message)
                .timestamp(Instant.now())
                .fieldErrors(fieldErrors)
                .build();
    }
}