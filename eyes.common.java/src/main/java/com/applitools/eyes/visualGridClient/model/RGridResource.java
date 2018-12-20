package com.applitools.eyes.visualGridClient.model;

import com.applitools.eyes.Logger;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;

public class RGridResource {

    @JsonIgnore
    private String url;

    @JsonInclude
    private String contentType;

    @JsonIgnore
    private byte[] content;

    @JsonInclude
    private String sha256;

    @JsonInclude
    private String hashFormat = "sha256";

    @JsonIgnore
    private Logger logger;


    public String getUrl() {
        return url;
    }

    public RGridResource(String url, String contentType, byte[] content, Logger logger) {
        this.url = url;
        this.contentType = contentType;
        this.content = content;
        this.logger = logger;
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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        ArgumentGuard.notNull(contentType, "content");
        this.content = content;
    }

    @JsonProperty("hash")
    public String getSha256() {
        if (sha256 == null) {
            this.sha256 = GeneralUtils.getSha256hash(content);
            this.logger.log("Computed hash(" + this.sha256 + ") for url - " + this.url + " content length: " + this.content.length);
        }
        return sha256;
    }

    public String getHashFormat() {
        return hashFormat;
    }

}

