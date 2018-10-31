package com.applitools.renderingGrid;

import java.util.List;

public class RGridDom {

    private String domNodes = null;

    private List<String> resources = null;

    private String sha256hash = null;

    private String contentAsCdt = null;

    public String getDomNodes() {
        return domNodes;
    }

    public void setDomNodes(String domNodes) {
        this.domNodes = domNodes;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public String getSha256hash() {
        return sha256hash;
    }

    public void setSha256hash(String sha256hash) {
        this.sha256hash = sha256hash;
    }

    public String getContentAsCdt() {
        return contentAsCdt;
    }

    public void setContentAsCdt(String contentAsCdt) {
        this.contentAsCdt = contentAsCdt;
    }
}
