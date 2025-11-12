package com.github.heroslender.hero_api.exceptions;

public class PluginNotFoundException extends ResourceNotFoundException {

    public PluginNotFoundException(Long id) {
        super("Could not find plugin with id " + id);
    }
    public PluginNotFoundException(Long id) {
        super("Could not find plugin with id '" + id + "'");
    }
}