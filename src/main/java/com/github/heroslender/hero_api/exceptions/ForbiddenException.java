package com.github.heroslender.hero_api.exceptions;

public class ForbiddenException extends RestApiExcetion {

  public ForbiddenException() {
    this(null);
  }

  public ForbiddenException(String message) {
    super((short) 403, "Forbidden", message);
  }
}