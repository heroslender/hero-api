package com.github.heroslender.hero_api.repository;

import com.github.heroslender.hero_api.entity.PluginVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PluginVersionRepository extends JpaRepository<PluginVersion, Long> {
}
