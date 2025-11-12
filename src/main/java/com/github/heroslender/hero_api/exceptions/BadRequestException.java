package com.github.heroslender.hero_api.exceptions;

public class BadRequestException extends RestApiExcetion {

    public BadRequestException(String message) {
        super((short) 400, "Bad request", message);
    }
}