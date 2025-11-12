package com.github.heroslender.hero_api.exceptions;

public class PluginVersionNotFoundException extends ResourceNotFoundException {

    public PluginVersionNotFoundException(String tag) {
        super("Could not find plugin version with tag '" + tag + "'");
    }
}