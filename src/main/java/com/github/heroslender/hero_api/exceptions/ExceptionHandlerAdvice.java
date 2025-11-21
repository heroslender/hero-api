package com.github.heroslender.hero_api.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestControllerAdvice
class ExceptionHandlerAdvice {
    private final Logger log = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiExceptionDTO notFoundHandler(ResourceNotFoundException ex) {
        return toDto(ex);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiExceptionDTO badRequestFoundHandler(BadRequestException ex) {
        return toDto(ex);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ApiExceptionDTO unauthorizedHandler(UnauthorizedException ex) {
        return toDto(ex);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ApiExceptionDTO forbiddenHandler(ForbiddenException ex) {
        return toDto(ex);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ApiExceptionDTO exHandler(Exception ex) {
        log.error("Unhandled exception!", ex);
        return new ApiExceptionDTO((short) 500, formatTimestamp(System.currentTimeMillis()), "Internal Server Error");
    }

    private ApiExceptionDTO toDto(RestApiExcetion ex) {
        return new ApiExceptionDTO(
                ex.getStatusCode(),
                formatTimestamp(ex.getTimestamp()),
                ex.getError(),
                ex.getMessage()
        );
    }

    private String formatTimestamp(long timestamp) {
        Date date = new Date(timestamp);
        Format format = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSSZ");
        return format.format(date);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record ApiExceptionDTO(
            short code,
            String timestamp,
            String error,
            String message
    ) {

        private ApiExceptionDTO(short code, String timestamp, String error) {
            this(code, timestamp, error, null);
        }
    }
}