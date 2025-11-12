package com.github.heroslender.hero_api.database.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class PluginEntity {

    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    private String name;

    @OneToMany(mappedBy = "plugin", fetch = FetchType.LAZY)
    private List<PluginVersionEntity> versions = new ArrayList<>();

    public PluginEntity() {
    }

    public PluginEntity(String name) {
        this.name = name;
    }

    public PluginEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PluginEntity plugin = (PluginEntity) o;
        return Objects.equals(getId(), plugin.getId()) && Objects.equals(getName(), plugin.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return "Plugin{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public List<PluginVersionEntity> getVersions() {
        return versions;
    }

    public void setVersions(List<PluginVersionEntity> versions) {
        this.versions = versions;
    }
}
