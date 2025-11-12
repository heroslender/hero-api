package com.github.heroslender.hero_api.exceptions;

public class DuplicatePluginVersionException extends BadRequestException {

    public DuplicatePluginVersionException(String tag) {
        super("Plugin already has a version with tag '" + tag + "'.");
    }
}