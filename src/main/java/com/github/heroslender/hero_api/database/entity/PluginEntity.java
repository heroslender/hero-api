package com.github.heroslender.hero_api.database.entity;

import com.github.heroslender.hero_api.model.PluginVisibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "plugins")
public class PluginEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private UserEntity owner;

    @Column(nullable = false)
    @ColumnDefault("'PUBLIC'")
    @Enumerated(EnumType.STRING)
    private PluginVisibility visibility = PluginVisibility.PUBLIC;

    private Float price;
    private Float promoPrice;
    private String tagline;
    @Lob
    @Column(length = 65536)
    private String description;

    @OneToMany(mappedBy = "plugin", fetch = FetchType.LAZY)
    private List<PluginVersionEntity> versions = new ArrayList<>();

    public PluginEntity(String id) {
        this(id, id, null, PluginVisibility.PUBLIC, 0.0F, 0.0F, "", "", Collections.emptyList());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PluginEntity that = (PluginEntity) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getOwner().getId(), that.getOwner().getId())
                && Objects.equals(getName(), that.getName())
                && Objects.equals(getPrice(), that.getPrice())
                && Objects.equals(getPromoPrice(), that.getPromoPrice())
                && Objects.equals(getTagline(), that.getTagline())
                && Objects.equals(getDescription(), that.getDescription())
                && getVisibility() == that.getVisibility();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOwner().getId(), getName(), getPrice(), getPromoPrice(), getTagline(), getDescription(), getVisibility());
    }

    @Override
    public String toString() {
        return "PluginEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", owner=" + owner.getId() +
                ", price=" + price +
                ", promoPrice=" + promoPrice +
                ", tagline='" + tagline + '\'' +
                ", description='" + description + '\'' +
                ", visibility=" + visibility +
                '}';
    }
}
