package com.github.heroslender.hero_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfiguration {
    @Bean
    Clock getClock() {
        return Clock.systemDefaultZone();
    }
}

