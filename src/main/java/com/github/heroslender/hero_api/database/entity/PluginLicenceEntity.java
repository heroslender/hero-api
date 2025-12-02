package com.github.heroslender.hero_api.database.entity;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
public class PluginLicenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long createdAt;
    private Long duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plugin", nullable = false)
    private PluginEntity plugin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner", nullable = false)
    private UserEntity owner;

    public PluginLicenceEntity() {
    }

    public PluginLicenceEntity(Long createdAt, Long duration, PluginEntity plugin, UserEntity owner) {
        this.createdAt = createdAt;
        this.duration = duration;
        this.plugin = plugin;
        this.owner = owner;
    }

    public PluginLicenceEntity(UUID id, Long createdAt, Long duration, PluginEntity plugin, UserEntity owner) {
        this.id = id;
        this.createdAt = createdAt;
        this.duration = duration;
        this.plugin = plugin;
        this.owner = owner;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public PluginEntity getPlugin() {
        return plugin;
    }

    public void setPlugin(PluginEntity plugin) {
        this.plugin = plugin;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PluginLicenceEntity that = (PluginLicenceEntity) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getCreatedAt(), that.getCreatedAt()) && Objects.equals(getDuration(), that.getDuration()) && Objects.equals(getPlugin(), that.getPlugin()) && Objects.equals(getOwner(), that.getOwner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCreatedAt(), getDuration(), getPlugin(), getOwner());
    }

    @Override
    public String toString() {
        return "PluginLicence{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", duration=" + duration +
                '}';
    }
}
