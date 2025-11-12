package com.github.heroslender.hero_api.config;

import com.github.heroslender.hero_api.database.entity.PluginEntity;
import com.github.heroslender.hero_api.database.repository.PluginRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadPluginsConfiguration {
    @Bean
    CommandLineRunner initDatabase(PluginRepository repository) {
        return args -> {
            repository.findByName("Crates").orElseGet(() -> repository.save(new PluginEntity("Crates")));
            repository.findByName("Trading").orElseGet(() -> repository.save(new PluginEntity("Trading")));
        };
    }
}
