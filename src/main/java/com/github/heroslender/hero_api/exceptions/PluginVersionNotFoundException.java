package com.github.heroslender.hero_api.exceptions;

public class PluginVersionNotFoundException extends RuntimeException {

  public PluginVersionNotFoundException(String id) {
    super("Could not find plugin version with tag " + id);
  }
}