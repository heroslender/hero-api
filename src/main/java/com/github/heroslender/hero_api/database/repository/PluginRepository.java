package com.github.heroslender.hero_api.database.repository;

import com.github.heroslender.hero_api.database.entity.PluginEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PluginRepository extends JpaRepository<PluginEntity, String> {
}
