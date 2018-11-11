package com.applitools.eyes.selenium.rendering;

public class RenderingGridService extends Thread{

    private int concurrentRequests = 3;

    RenderingGridService(int concurrentRequests){
        concurrentRequests = concurrentRequests * 5; // 1 Eyes session per 5 Rendering grid requests
    }
}
