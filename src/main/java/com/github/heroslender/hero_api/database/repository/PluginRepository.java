package com.github.heroslender.hero_api.database.repository;

import com.github.heroslender.hero_api.database.entity.PluginEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PluginRepository extends JpaRepository<PluginEntity, Long> {
    Optional<PluginEntity> findByName(String name);
}
