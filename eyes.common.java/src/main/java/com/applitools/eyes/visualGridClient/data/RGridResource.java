package com.applitools.eyes.visualGridClient.data;

import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RGridResource {

    @JsonIgnore
    private String url = null;

    @JsonInclude
    private String contentType = null;

    @JsonIgnore
    private Byte[] content = null;

    @JsonInclude
    private String sha256hash = null;

    @JsonInclude
    private String hashFormat = "sha256";


    public String getUrl() {
        return url;
    }

    public RGridResource(String url, String contentType, Byte[] content) {
        this.url = url;
        this.contentType = contentType;
        this.content = content;
        this.sha256hash = getSha256hash();

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
    public String getSha256hash() {
        if (sha256hash == null) {
            this.sha256hash = GeneralUtils.getSha256hash(content);
        }
        return sha256hash;
    }

    public String getHashFormat() {
        return hashFormat;
    }
}
