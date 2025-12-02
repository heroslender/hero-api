package com.github.heroslender.hero_api.database.repository;

import com.github.heroslender.hero_api.database.entity.PluginLicenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PluginLicenceRepository extends JpaRepository<PluginLicenceEntity, UUID> {

    List<PluginLicenceEntity> findByOwnerId(Long owner);
}
