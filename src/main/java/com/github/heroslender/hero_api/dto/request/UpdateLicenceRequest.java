package com.github.heroslender.hero_api.dto.request;

public record UpdateLicenceRequest(
        Long duration,
        Long ownerId
) {
}
