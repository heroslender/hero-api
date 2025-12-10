package com.github.heroslender.hero_api.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PluginVersionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plugin_id")
    private PluginEntity plugin;
    private String tag;
    private Long releasedAt;
    private String releaseTitle;
    private String releaseNotes;
    private Integer downloadCount;

    public PluginVersionEntity(PluginEntity plugin, String tag, Long releasedAt, String releaseTitle, String releaseNotes, Integer downloadCount) {
        this.plugin = plugin;
        this.tag = tag;
        this.releasedAt = releasedAt;
        this.releaseTitle = releaseTitle;
        this.releaseNotes = releaseNotes;
        this.downloadCount = downloadCount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PluginVersionEntity that = (PluginVersionEntity) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getPlugin().getName(), that.getPlugin().getName())
                && Objects.equals(getTag(), that.getTag())
                && Objects.equals(getReleasedAt(), that.getReleasedAt())
                && Objects.equals(getReleaseTitle(), that.getReleaseTitle())
                && Objects.equals(getReleaseNotes(), that.getReleaseNotes())
                && Objects.equals(getDownloadCount(), that.getDownloadCount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPlugin().getName(), getTag(), getReleasedAt(), getReleaseTitle(), getReleaseNotes(), getDownloadCount());
    }

    @Override
    public String toString() {
        return "PluginVersion{" +
                "id=" + id +
                ", plugin=" + plugin.getName() +
                ", tag='" + tag + '\'' +
                ", releasedAt=" + releasedAt +
                ", releaseTitle='" + releaseTitle + '\'' +
                ", releaseNotes='" + releaseNotes + '\'' +
                ", downloadCount=" + downloadCount +
                '}';
    }
}
