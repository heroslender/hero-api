package com.github.heroslender.hero_api.database.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class PluginEntity {
    @Id
    private String name;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", unique = false)
    private UserEntity owner;
    private String displayName;
    private String descrition;

    @OneToMany(mappedBy = "plugin", fetch = FetchType.LAZY)
    private List<PluginVersionEntity> versions = new ArrayList<>();

    public PluginEntity() {
    }

    public PluginEntity(String name) {
        this(name, name, null);
    }

    public PluginEntity(String name, String displayName, String descrition) {
        this.name = name;
        this.displayName = displayName;
        this.descrition = descrition;
    }

    public String getName() {
        return name;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescrition() {
        return descrition;
    }

    public void setDescrition(String descrition) {
        this.descrition = descrition;
    }

    public List<PluginVersionEntity> getVersions() {
        return versions;
    }

    public void setVersions(List<PluginVersionEntity> versions) {
        this.versions = versions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PluginEntity that = (PluginEntity) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getDisplayName(), that.getDisplayName()) && Objects.equals(getDescrition(), that.getDescrition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDisplayName(), getDescrition());
    }

    @Override
    public String toString() {
        return "PluginEntity{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", descrition='" + descrition + '\'' +
                '}';
    }
}
