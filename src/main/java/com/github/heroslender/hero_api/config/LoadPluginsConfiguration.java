package com.github.heroslender.hero_api.config;

import com.github.heroslender.hero_api.entity.Plugin;
import com.github.heroslender.hero_api.repository.PluginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadPluginsConfiguration {
    private static final Logger log = LoggerFactory.getLogger(LoadPluginsConfiguration.class);

    @Bean
    CommandLineRunner initDatabase(PluginRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(new Plugin("Crates")));
            log.info("Preloading " + repository.save(new Plugin("Trading")));
        };
    }
}
