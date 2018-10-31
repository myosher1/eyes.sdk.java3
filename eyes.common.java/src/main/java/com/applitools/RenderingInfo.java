package com.applitools;

public class RenderingInfo {

    private String serviceUrl = null;
    private String accessToken = null;
    private String resultsUrl = null;

    public RenderingInfo(String serviceUrl, String accessToken, String resultsUrl) {
        this.serviceUrl = serviceUrl;
        this.accessToken = accessToken;
        this.resultsUrl = resultsUrl;
    }

    public RenderingInfo() {
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getResultsUrl() {
        return resultsUrl;
    }

    public void setResultsUrl(String resultsUrl) {
        this.resultsUrl = resultsUrl;
    }
}
