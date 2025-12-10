package com.github.heroslender.hero_api.database.entity;

import com.github.heroslender.hero_api.model.PluginVisibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public PluginEntity(String name) {
        this(name, PluginVisibility.PUBLIC, name, null);
    }

    public PluginEntity(String name, PluginVisibility visibility, String displayName, String descrition) {
        this.name = name;
        this.visibility = visibility;
        this.displayName = displayName;
        this.descrition = descrition;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PluginEntity that = (PluginEntity) o;
        return Objects.equals(getName(), that.getName())
                && Objects.equals(getOwner().getId(), that.getOwner().getId())
                && Objects.equals(getDisplayName(), that.getDisplayName())
                && Objects.equals(getDescrition(), that.getDescrition())
                && getVisibility() == that.getVisibility();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getOwner().getId(), getDisplayName(), getDescrition(), getVisibility());
    }


    @Override
    public String toString() {
        return "PluginEntity{" +
                "name='" + name + '\'' +
                ", owner=" + owner.getId() +
                ", displayName='" + displayName + '\'' +
                ", descrition='" + descrition + '\'' +
                ", visibility=" + visibility +
                '}';
    }
}
