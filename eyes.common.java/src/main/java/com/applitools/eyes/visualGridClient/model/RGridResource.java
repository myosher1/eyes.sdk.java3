package com.applitools.eyes.visualGridClient.model;

import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RGridResource {

    @JsonIgnore
    private String url;

    @JsonInclude
    private String contentType;

    @JsonIgnore
    private Byte[] content;

    @JsonInclude
    private String sha256;

    @JsonInclude
    private String hashFormat = "sha256";


    public String getUrl() {
        return url;
    }

    public RGridResource(String url, String contentType, Byte[] content) {
        this.url = url;
        this.contentType = contentType;
        this.content = content;
        this.sha256 = getSha256();
    }

    public void setUrl(String url) {
        ArgumentGuard.notNull(url, "url");
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        ArgumentGuard.notNull(contentType, "contentType");
        this.contentType = contentType;
    }

    public Byte[] getContent() {
        return content;
    }

    public void setContent(Byte[] content) {
        ArgumentGuard.notNull(contentType, "content");
        this.content = content;
    }

    @JsonProperty("hash")
    public String getSha256() {
        if (sha256 == null) {
            this.sha256 = GeneralUtils.getSha256hash(content);
        }
        return sha256;
    }

    public String getHashFormat() {
        return hashFormat;
    }

}

