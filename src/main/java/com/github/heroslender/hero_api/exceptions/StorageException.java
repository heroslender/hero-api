package com.github.heroslender.hero_api.exceptions;

public class StorageException extends BadRequestException {

    public StorageException(String message) {
        super(message);
    }
}