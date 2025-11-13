package com.github.heroslender.hero_api.exceptions;

public class UnauthorizedException extends RestApiExcetion {

  public UnauthorizedException() {
    super((short) 403, "Forbidden", null);
  }

  public UnauthorizedException(String message) {
    super((short) 403, "Forbidden", message);
  }
}