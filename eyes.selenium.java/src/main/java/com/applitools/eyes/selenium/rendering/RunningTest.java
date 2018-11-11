package com.applitools.eyes.selenium.rendering;

import com.applitools.eyes.EyesBase;

import java.util.ArrayList;
import java.util.List;

class RunningTest {
    private List<Task> taskList = new ArrayList<>();
    private EyesBase eyes;
    private RenderingConfiguration.RenderBrowserInfo browserInfo;
    private boolean isTestOpen = false;


    public RunningTest(EyesBase eyes, RenderingConfiguration.RenderBrowserInfo browserInfo) {
        this.eyes = eyes;
        this.browserInfo = browserInfo;
    }

    public boolean isTestOpen() {
        return isTestOpen;
    }

    public void setTestOpen(boolean testOpen) {
        isTestOpen = testOpen;
    }

    void addTask(Task task) {
        taskList.add(task);
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    int getMark() {
        int mark = 0;
        for (Task task : this.getTaskList()) {
            if (task.getLinkToImage() != null) {
                mark++;
            }
        }
        return mark;
    }
}