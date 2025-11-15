package com.github.heroslender.hero_api.exceptions;

public class UnauthorizedException extends RestApiExcetion {

    public UnauthorizedException() {
        this(null);
    }

    public UnauthorizedException(String message) {
        super((short) 401, "Unauthorized", message);
    }
}