package com.github.heroslender.hero_api.database.repository;

import com.github.heroslender.hero_api.database.entity.PluginVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PluginVersionRepository extends JpaRepository<PluginVersionEntity, Long> {

    List<PluginVersionEntity> findByPluginId(String pluginId);
}
