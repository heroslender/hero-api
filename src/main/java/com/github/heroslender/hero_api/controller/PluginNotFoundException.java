package com.github.heroslender.hero_api.controller;

class PluginNotFoundException extends RuntimeException {

  PluginNotFoundException(Long id) {
    super("Could not find plugin with id " + id);
  }
}