package com.applitools.eyes.visualGridClient.services;

import com.applitools.eyes.Logger;
import com.applitools.utils.GeneralUtils;

import java.util.concurrent.Semaphore;

public class EyesCloserService extends EyesBaseService {


    public EyesCloserService(String ServiceName, Logger logger, ThreadGroup servicesGroup, int threadPoolSize, EyesServiceListener listener) {
        super(ServiceName, servicesGroup, logger, threadPoolSize, listener);
    }

    public void acquire(Semaphore semaphore) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            GeneralUtils.logExceptionStackTrace(e);
        }
    }
}
