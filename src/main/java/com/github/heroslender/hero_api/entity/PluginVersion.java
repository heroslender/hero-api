package com.github.heroslender.hero_api.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class PluginVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plugin_id")
    private Plugin plugin;
    private String tag;
    private Long releasedAt;
    private String releaseTitle;
    private String releaseNotes;
    private String downloadUrl;
    private Integer downloadCount;

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public PluginVersion() {
    }

    public PluginVersion(Plugin plugin, String tag, Long releasedAt, String releaseTitle, String releaseNotes, String downloadUrl, Integer downloadCount) {
        this.plugin = plugin;
        this.tag = tag;
        this.releasedAt = releasedAt;
        this.releaseTitle = releaseTitle;
        this.releaseNotes = releaseNotes;
        this.downloadUrl = downloadUrl;
        this.downloadCount = downloadCount;
    }

    public PluginVersion(Long id, Plugin plugin, String tag, Long releasedAt, String releaseTitle, String releaseNotes, String downloadUrl, Integer downloadCount) {
        this.id = id;
        this.plugin = plugin;
        this.tag = tag;
        this.releasedAt = releasedAt;
        this.releaseTitle = releaseTitle;
        this.releaseNotes = releaseNotes;
        this.downloadUrl = downloadUrl;
        this.downloadCount = downloadCount;
    }

    public Long getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getReleasedAt() {
        return releasedAt;
    }

    public void setReleasedAt(Long releasedAt) {
        this.releasedAt = releasedAt;
    }

    public String getReleaseTitle() {
        return releaseTitle;
    }

    public void setReleaseTitle(String releaseTitle) {
        this.releaseTitle = releaseTitle;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PluginVersion that = (PluginVersion) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getTag(), that.getTag()) && Objects.equals(getReleasedAt(), that.getReleasedAt()) && Objects.equals(getReleaseTitle(), that.getReleaseTitle()) && Objects.equals(getReleaseNotes(), that.getReleaseNotes()) && Objects.equals(getDownloadUrl(), that.getDownloadUrl()) && Objects.equals(getDownloadCount(), that.getDownloadCount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTag(), getReleasedAt(), getReleaseTitle(), getReleaseNotes(), getDownloadUrl(), getDownloadCount());
    }

    @Override
    public String toString() {
        return "PluginVersion{" +
                "id=" + id +
                ", tag='" + tag + '\'' +
                ", releasedAt=" + releasedAt +
                ", releaseTitle='" + releaseTitle + '\'' +
                ", releaseNotes='" + releaseNotes + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", downloadCount=" + downloadCount +
                '}';
    }
}
