package com.github.heroslender.hero_api.config;

import com.github.heroslender.hero_api.entity.Plugin;
import com.github.heroslender.hero_api.repository.PluginRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadPluginsConfiguration {
    @Bean
    CommandLineRunner initDatabase(PluginRepository repository) {
        return args -> {
            repository.findByName("Crates").orElseGet(() -> repository.save(new Plugin("Crates")));
            repository.findByName("Trading").orElseGet(() -> repository.save(new Plugin("Trading")));
        };
    }
}
