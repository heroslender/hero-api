package com.github.heroslender.hero_api.exceptions;

public class ResourceNotFoundException extends RestApiExcetion {

  public ResourceNotFoundException(String message) {
    super((short) 404, "Resource not found", message);
  }
}