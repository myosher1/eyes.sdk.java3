package com.applitools.eyes.visualGridClient.model;

import com.applitools.eyes.Logger;
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
    private byte[] content;

    @JsonProperty("hash")
    private String sha256;

    @JsonInclude
    private final String hashFormat = "sha256";

    @JsonIgnore
    private Logger logger;


    public String getUrl() {
        return url;
    }

    public RGridResource(String url, String contentType, byte[] content, Logger logger, String msg) {

        this.contentType = contentType;
        this.content = content;
        this.logger = logger;
        this.sha256 = GeneralUtils.getSha256hash(content);
//        if (msg.startsWith("RGridDom")) {
//            logger.log("creating resource - "+url + " from "+msg);
//            logger.log("contentType - "+this.contentType);
//            logger.log("content :\n"+new String(content));
//        }
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }


    public byte[] getContent() {
        return content;
    }

    public String getHashFormat() {
        return hashFormat;
    }

    public String getSha256() {
        return sha256;
    }
}

