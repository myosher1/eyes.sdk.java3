package com.applitools.eyes.visualGridClient.services;

import com.applitools.eyes.Logger;

public class EyesCheckerService extends EyesBaseService{

    public EyesCheckerService(String ServiceName, Logger logger, ThreadGroup servicesGroup, int threadPoolSize, EyesBaseService.EyesServiceListener listener) {
        super(ServiceName, servicesGroup, logger, threadPoolSize, listener);
    }
}
