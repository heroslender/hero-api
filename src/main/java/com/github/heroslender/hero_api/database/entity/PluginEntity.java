package com.github.heroslender.hero_api.database.entity;

import com.github.heroslender.hero_api.model.PluginVisibility;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

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

    @Column(nullable = false)
    @ColumnDefault("'PUBLIC'")
    @Enumerated(EnumType.STRING)
    private PluginVisibility visibility = PluginVisibility.PUBLIC;

    @OneToMany(mappedBy = "plugin", fetch = FetchType.LAZY)
    private List<PluginVersionEntity> versions = new ArrayList<>();

    public PluginEntity() {
    }

    public PluginEntity(String name) {
        this(name, PluginVisibility.PUBLIC, name, null);
    }

    public PluginEntity(String name, PluginVisibility visibility, String displayName, String descrition) {
        this.name = name;
        this.visibility = visibility;
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

    public PluginVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(PluginVisibility visibility) {
        this.visibility = visibility;
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
