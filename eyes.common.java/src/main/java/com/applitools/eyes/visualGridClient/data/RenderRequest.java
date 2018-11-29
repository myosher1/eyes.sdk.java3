package com.applitools.eyes.visualGridClient.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class RenderRequest {

    @JsonIgnore
    private final Task task;

    @JsonIgnore
    private double id;

    @JsonInclude
    private String webHook;

    @JsonInclude
    private String url;

    @JsonInclude
    private RGridDom dom;

    @JsonInclude
    private Map<String, RGridResource> resources;

    @JsonInclude
    private RenderInfo renderInfo;

    @JsonIgnore
    private String platform;

    @JsonIgnore
    private String browserName;

    @JsonInclude
    private Object scriptHooks;

    @JsonInclude
    private String[] selectorsToFindRegionsFor;

    @JsonInclude
    private boolean sendDom;

    public RenderRequest(double id, String webHook, String url, RGridDom dom, Map<String, RGridResource> resources, RenderInfo renderInfo, String platform, String browserName, Object scriptHooks, String[] selectorsToFindRegionsFor, boolean sendDom, Task task) {
        this.id = id;
        this.webHook = webHook;
        this.url = url;
        this.dom = dom;
        this.resources = resources;
        this.renderInfo = renderInfo;
        this.platform = platform;
        this.browserName = browserName;
        this.scriptHooks = scriptHooks;
        this.selectorsToFindRegionsFor = selectorsToFindRegionsFor;
        this.sendDom = sendDom;
        this.task = task;

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RGridDom getDom() {
        return dom;
    }

    public void setDom(RGridDom dom) {
        this.dom = dom;
    }

    public Map<String, RGridResource> getResources() {
        return resources;
    }

    public void setResources(Map<String, RGridResource> resources) {
        this.resources = resources;
    }

    public RenderInfo getRenderInfo() {
        return renderInfo;
    }

    public void setRenderInfo(RenderInfo renderInfo) {
        this.renderInfo = renderInfo;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public Object getScriptHooks() {
        return scriptHooks;
    }

    public void setScriptHooks(Object scriptHooks) {
        this.scriptHooks = scriptHooks;
    }

    public String[] getSelectorsToFindRegionsFor() {
        return selectorsToFindRegionsFor;
    }

    public void setSelectorsToFindRegionsFor(String[] selectorsToFindRegionsFor) {
        this.selectorsToFindRegionsFor = selectorsToFindRegionsFor;
    }

    public boolean isSendDom() {
        return sendDom;
    }

    public void setSendDom(boolean sendDom) {
        this.sendDom = sendDom;
    }

    @JsonProperty("browser")
    public Map<String,String> getBrowser(){
        Map<String,String> map = new HashMap<>();
        map.put("name", this.browserName);
        map.put("platform", this.platform);
        return map;
    }

    public Task getTask() {
        return task;
    }
}
