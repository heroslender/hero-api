package com.github.heroslender.hero_api.exceptions;

public class SessionExpiredException extends UnauthorizedException {
    public SessionExpiredException(String message) {
        super(message);
    }

    public SessionExpiredException() {
        super("Your session has expired. Please log back in again.");
    }
}
