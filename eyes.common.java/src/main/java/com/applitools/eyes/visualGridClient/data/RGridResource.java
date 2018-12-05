package com.applitools.eyes.visualGridClient.data;

import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.MalformedURLException;
import java.net.URL;

public class RGridResource {

    @JsonIgnore
    private URL url = null;

    @JsonIgnore
    private URL baseUrl = null;

    @JsonInclude
    private String contentType = null;

    @JsonIgnore
    private Byte[] content = null;

    @JsonInclude
    private String sha256 = null;

    @JsonInclude
    private String hashFormat = "sha256";


    public URL getUrl() {
        return url;
    }

    public RGridResource(URL url, String contentType, Byte[] content) {
        this.url = url;
        this.contentType = contentType;
        this.content = content;
        this.sha256 = getSha256();

    }

    public void setUrl(URL url) {
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

    public URL getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URL baseUrl) throws MalformedURLException {
        this.baseUrl = baseUrl;
            this.url = new URL(baseUrl, url.toString());
    }
}
