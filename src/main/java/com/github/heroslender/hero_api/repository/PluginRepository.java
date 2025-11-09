package com.github.heroslender.hero_api.repository;

import com.github.heroslender.hero_api.entity.Plugin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PluginRepository extends JpaRepository<Plugin, Long> {
    Optional<Plugin> findByName(String name);
}
