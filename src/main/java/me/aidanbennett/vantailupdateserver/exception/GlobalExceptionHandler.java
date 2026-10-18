package me.aidanbennett.vantailupdateserver.exception;

import me.aidanbennett.vantailupdateserver.dto.error.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UpdateNotFoundException.class)
    public ApiErrorResponse handleUpdateNotFound(UpdateNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(InvalidPageException.class)
    public ApiErrorResponse handleInvalidPage(InvalidPageException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private ApiErrorResponse error(HttpStatus status, String message) {
        return new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                Instant.now()
        );
    }
}
