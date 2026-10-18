package me.aidanbennett.vantailupdateserver.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant timestamp
) {
}
