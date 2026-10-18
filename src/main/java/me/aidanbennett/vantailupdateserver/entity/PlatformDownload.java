package me.aidanbennett.vantailupdateserver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PlatformDownload {

    @Column(nullable = false, length = 2048)
    private String url;

    @Column(nullable = false, columnDefinition = "text")
    private String signature;

    protected PlatformDownload() {
    }

    public PlatformDownload(String url, String signature) {
        this.url = url;
        this.signature = signature;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
