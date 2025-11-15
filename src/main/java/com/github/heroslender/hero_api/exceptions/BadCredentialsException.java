package com.github.heroslender.hero_api.exceptions;

public class BadCredentialsException extends UnauthorizedException {
    public BadCredentialsException(String message) {
        super(message);
    }

    public BadCredentialsException() {
        super("Invalid username/password!");
    }
}
