package com.applitools.renderingGrid;

import com.applitools.utils.ArgumentGuard;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RGridResource {

    private String url = null;

    private String contentType = null;

    private byte[] content = null;

    private String sha256hash = null;

    public String getUrl() {
        return url;
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

    public String getSha256hash() {
        if (sha256hash == null) {
            byte[] buffer = new byte[8192];
            int count;
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("SHA-256");
                BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(content));
                while ((count = bis.read(buffer)) > 0) {
                    digest.update(buffer, 0, count);
                }
                bis.close();

                this.sha256hash = new String(digest.digest());
            } catch (NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            }
        }
        return sha256hash;
    }
}
