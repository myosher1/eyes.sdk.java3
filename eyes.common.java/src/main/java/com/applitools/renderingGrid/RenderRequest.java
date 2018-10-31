package com.applitools.renderingGrid;

public class RenderRequest {

    private String webHook;
    private String url;
    private RGridDom dom;
    private RGridResource resource;
    private RenderInfo renderInfo;
    private String platform;
    private String browserName;
    private Object scriptHooks;
    private String[] selectorsToFindRegionsFor;
    private boolean sendDom;

    public RenderRequest(String webHook, String url, RGridDom dom, RGridResource resource, RenderInfo renderInfo, String platform, String browserName, Object scriptHooks, String[] selectorsToFindRegionsFor, boolean sendDom) {
        this.webHook = webHook;
        this.url = url;
        this.dom = dom;
        this.resource = resource;
        this.renderInfo = renderInfo;
        this.platform = platform;
        this.browserName = browserName;
        this.scriptHooks = scriptHooks;
        this.selectorsToFindRegionsFor = selectorsToFindRegionsFor;
        this.sendDom = sendDom;
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

    public RGridResource getResource() {
        return resource;
    }

    public void setResource(RGridResource resource) {
        this.resource = resource;
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
}
