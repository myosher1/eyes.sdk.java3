package com.applitools.eyes.selenium.rendering;

import java.util.ArrayList;
import java.util.List;

public class RenderingConfiguration {

    public enum BrowserType{CHROME, FIREFOX}

    private List<RenderBrowserInfo> browsersInfo = new ArrayList<>();

    private int concurrentSessions = 3;

    public int getConcurrentSessions() {
        return concurrentSessions;
    }

    public void setConcurrentSessions(int concurrentSessions) {
        this.concurrentSessions = concurrentSessions;
    }

    public RenderingConfiguration addBrowser(int width, int height, BrowserType browserType){
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(width, height, browserType);
        browsersInfo.add(browserInfo);
        return this;
    }


    public class RenderBrowserInfo{
        private int width;
        private int height;
        private BrowserType browserType;

        public RenderBrowserInfo(int width, int height, BrowserType browserType) {
            this.width = width;
            this.height = height;
            this.browserType = browserType;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public BrowserType getBrowserType() {
            return browserType;
        }

        public void setBrowserType(BrowserType browserType) {
            this.browserType = browserType;
        }
    }

    public List<RenderBrowserInfo> getBrowsersInfo() {
        return browsersInfo;
    }

    public String toJson(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < this.browsersInfo.size(); i++) {
            RenderBrowserInfo renderBrowserInfo = browsersInfo.get(i);
            sb.append("{");
            sb.append("width: ").append(renderBrowserInfo.width).append(",");
            sb.append("height: ").append(renderBrowserInfo.height).append(",");
            sb.append("name: ");
            switch (renderBrowserInfo.browserType){
                case CHROME:
                    sb.append("chrome");
                    break;
                case FIREFOX:
                    sb.append("firefox");
                    break;
            }
            sb.append("}");
            if(i != (this.browsersInfo.size() -1)){
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
