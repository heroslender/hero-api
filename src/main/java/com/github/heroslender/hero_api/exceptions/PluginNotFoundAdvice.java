package com.github.heroslender.hero_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class PluginNotFoundAdvice {

  @ExceptionHandler(PluginNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String pluginNotFoundHandler(PluginNotFoundException ex) {
    return ex.getMessage();
  }

  @ExceptionHandler(PluginVersionNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String pluginVersionNotFoundHandler(PluginVersionNotFoundException ex) {
    return ex.getMessage();
  }
}