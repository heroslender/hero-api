package com.github.heroslender.hero_api.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public PluginLicenceEntity(Long createdAt, Long duration, PluginEntity plugin, UserEntity owner) {
        this.createdAt = createdAt;
        this.duration = duration;
        this.plugin = plugin;
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PluginLicenceEntity that = (PluginLicenceEntity) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getCreatedAt(), that.getCreatedAt())
                && Objects.equals(getDuration(), that.getDuration())
                && Objects.equals(getPlugin().getId(), that.getPlugin().getId())
                && Objects.equals(getOwner().getId(), that.getOwner().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCreatedAt(), getDuration(), getPlugin().getId(), getOwner().getId());
    }

    @Override
    public String toString() {
        return "PluginLicenceEntity{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", duration=" + duration +
                ", plugin=" + plugin.getId() +
                ", owner=" + owner.getId() +
                '}';
    }
}
