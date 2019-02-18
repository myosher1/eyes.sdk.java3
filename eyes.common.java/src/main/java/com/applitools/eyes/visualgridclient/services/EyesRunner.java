package com.applitools.eyes.visualgridclient.services;

import com.applitools.eyes.LogHandler;
import com.applitools.eyes.Logger;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.visualgridclient.model.FileDebugResourceWriter;
import com.applitools.eyes.visualgridclient.model.TestResultSummary;

public abstract class EyesRunner {

    protected final Logger logger = new Logger();

    public abstract TestResultSummary getAllTestResults();

    public abstract TestResultSummary getAllTestResults(boolean shouldThrowException);

    public void setLogHandler(LogHandler logHandler) {
        logger.setLogHandler(logHandler);
        if (!logHandler.isOpen()) {
            logHandler.open();
        }
    }

    public Logger getLogger(){
      return this.logger;
    }


}
