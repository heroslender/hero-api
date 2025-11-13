package com.github.heroslender.hero_api.exceptions;

public class PluginNotFoundException extends ResourceNotFoundException {

    public PluginNotFoundException(String id) {
        super("Could not find plugin with id '" + id + "'");
    }
}