package com.applitools.eyes.visualGridClient.services;

import com.applitools.eyes.Logger;

public class EyesOpenerService extends EyesBaseService {
    

    public EyesOpenerService(String ServiceName, Logger logger, ThreadGroup servicesGroup, int threadPoolSize, EyesServiceListener listener) {
        super(ServiceName, servicesGroup, logger, threadPoolSize, listener);
    }

    
}
