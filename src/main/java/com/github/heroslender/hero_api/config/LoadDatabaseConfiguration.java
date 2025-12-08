package com.github.heroslender.hero_api.config;

import com.github.heroslender.hero_api.database.entity.PluginEntity;
import com.github.heroslender.hero_api.database.entity.PluginLicenceEntity;
import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.database.repository.PluginLicenceRepository;
import com.github.heroslender.hero_api.database.repository.PluginRepository;
import com.github.heroslender.hero_api.database.repository.UserRepository;
import com.github.heroslender.hero_api.model.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class LoadDatabaseConfiguration {
    @Bean
    CommandLineRunner initDatabase(PluginRepository repository, UserRepository userRepository, PluginLicenceRepository licenceRepository) {
        return args -> {
            repository.findByName("Crates").orElseGet(() -> repository.save(new PluginEntity("Crates")));
            repository.findByName("Trading").orElseGet(() -> repository.save(new PluginEntity("Trading")));

            UserEntity user = userRepository.findByUsername("heroslender");
            if (user == null) {
                user = new UserEntity("heroslender", "hero@email.com", "123", List.of(UserRole.DEVELOPER));
                user = userRepository.save(user);
            }

            if (licenceRepository.findAll().isEmpty()) {
                PluginLicenceEntity licence = licenceRepository.save(new PluginLicenceEntity(
                        System.currentTimeMillis(),
                        14L * 1000 * 60 * 60 * 24,
                        new PluginEntity("Crates"),
                        user
                ));

                System.out.println("Created licence with ID: " + licence.getId());
            } else {
                PluginLicenceEntity licence = licenceRepository.findAll().getFirst();
                System.out.println("Found licence with ID: " + licence.getId());
            }
        };
    }
}
