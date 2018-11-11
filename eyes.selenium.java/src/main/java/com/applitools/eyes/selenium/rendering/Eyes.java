package com.applitools.eyes.selenium.rendering;

import com.applitools.RenderingInfo;
import com.applitools.eyes.EyesBase;
import com.applitools.eyes.EyesScreenshot;
import com.applitools.eyes.RectangleSize;
import com.applitools.utils.ArgumentGuard;

import java.util.ArrayList;
import java.util.List;

public class Eyes extends EyesBase{

    private RenderingInfo renderInfo;

    enum EyesOpenStatus {OPEN, CLOSED, PARTIAL_OPEN, NONE}

    enum TaskType {OPEN, CHECK, CLOSE}

    @Override
    protected String getBaseAgentId() {
        return null;
    }

    @Override
    public String tryCaptureDom() {
        return null;
    }

    @Override
    protected RectangleSize getViewportSize() {
        return null;
    }

    @Override
    protected void setViewportSize(RectangleSize size) {

    }

    @Override
    protected String getInferredEnvironment() {
        return null;
    }

    @Override
    protected EyesScreenshot getScreenshot() {
        return null;
    }

    @Override
    protected String getTitle() {
        return null;
    }

    @Override
    protected String getAUTSessionId() {
        return null;
    }

    private EyesOpenStatus currentEyesOpenStatus = EyesOpenStatus.NONE;
    private RenderingConfiguration renderingConfiguration;
    private VisualGridManager visualGridClient;
    private List<RunningTest> testList = new ArrayList<>();

    public Eyes(RenderingConfiguration renderingConfiguration) {
        ArgumentGuard.notNull(renderingConfiguration, "renderingConfiguration");
        renderInfo = this.serverConnector.getRenderInfo();
        this.visualGridClient = VisualGridManager.getInstance(renderingConfiguration.getConcurrentSessions());
        this.renderingConfiguration = renderingConfiguration;

        for (RenderingConfiguration.RenderBrowserInfo browserInfo : renderingConfiguration.getBrowsersInfo()) {
            com.applitools.eyes.selenium.Eyes eyes = new com.applitools.eyes.selenium.Eyes();
            this.testList.add(new RunningTest(eyes, browserInfo));
        }
    }

    public void open() {

        this.visualGridClient.open(this);
    }

    RunningTest getNextTestToOpen() {
        RunningTest currentBestChoice = null;
        int currentBestMark = -1;
        for (RunningTest test : testList) {

            if (test.isTestOpen()) continue;

            if (test.getMark() > currentBestMark) {
                currentBestChoice = test;
            }
        }

        if (currentBestChoice == null) {
            currentBestChoice = testList.get(0);
        }

        return currentBestChoice;
    }


    public EyesOpenStatus getCurrentEyesOpenStatus() {
        return currentEyesOpenStatus;
    }

    public void setCurrentEyesOpenStatus(EyesOpenStatus currentEyesOpenStatus) {
        this.currentEyesOpenStatus = currentEyesOpenStatus;
    }
}
