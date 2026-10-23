package me.aidanbennett.vantailupdateserver.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "update_releases")
public class UpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String version;

    @Column(nullable = false, columnDefinition = "text")
    private String notes;

    @Column(name = "published_at", nullable = false)
    private Instant pubDate;

    @Column(name = "request_count", nullable = false)
    @ColumnDefault("0")
    private long requestCount = 0;

    @ElementCollection
    @CollectionTable(name = "update_platforms", joinColumns = @JoinColumn(name = "update_id"))
    @MapKeyColumn(name = "platform", length = 100)
    private Map<String, PlatformDownload> platforms = new LinkedHashMap<>();

    public UpdateEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getPubDate() {
        return pubDate;
    }

    public void setPubDate(Instant pubDate) {
        this.pubDate = pubDate;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(long requestCount) {
        this.requestCount = requestCount;
    }

    public Map<String, PlatformDownload> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Map<String, PlatformDownload> platforms) {
        this.platforms = platforms == null ? new LinkedHashMap<>() : new LinkedHashMap<>(platforms);
    }
}
