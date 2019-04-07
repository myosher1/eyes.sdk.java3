package com.applitools.eyes;

import com.applitools.eyes.visualgrid.model.RGridResource;
import com.applitools.eyes.visualgrid.model.RunningRender;
import com.applitools.utils.GeneralUtils;

import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PutFuture implements Future {

    private Future putFuture;
    private RGridResource resource;
    private RunningRender runningRender;
    private IServerConnector serverConnector;
    private Logger logger;

    private boolean isSentAlready = false;
    private int retryCount = 5;

    public PutFuture(RGridResource resource, RunningRender runningRender, IServerConnector serverConnector, Logger logger) {
        this.resource = resource;
        this.runningRender = runningRender;
        this.serverConnector = serverConnector;
        this.logger = logger;
    }

    public PutFuture(Future putFuture, RGridResource resource, RunningRender runningRender, IServerConnector serverConnector, Logger logger) {
        this(resource, runningRender, serverConnector, logger);
        this.putFuture = putFuture;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Boolean get() {
        if (this.putFuture == null) {
            IPutFuture newFuture = serverConnector.renderPutResource(runningRender, resource, null);
            this.putFuture = newFuture.getPutFuture();
        }
        if (!this.isSentAlready) {
            while (retryCount != 0) {
                try {
                    this.putFuture.get(20, TimeUnit.SECONDS);
                    break;
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    logger.verbose(e.getMessage() + " on hash: " + resource.getSha256());
                    retryCount--;
                    logger.verbose("Entering retry");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e1) {
                        GeneralUtils.logExceptionStackTrace(logger, e1);
                    }
                    IPutFuture newFuture = serverConnector.renderPutResource(runningRender, resource, null);
                    logger.log("fired retry");
                    this.putFuture = newFuture.getPutFuture();
                }
            }
        }
        this.isSentAlready = true;
        return true;
    }

    @Override
    public Boolean get(long timeout, TimeUnit unit) throws
            InterruptedException, ExecutionException, TimeoutException {
        if (!this.isSentAlready) {
            Object responseAsObject = this.putFuture.get(timeout, unit);
            if(responseAsObject instanceof Response){
                Response response = (Response) responseAsObject;
                int status = response.getStatus();
                if(status > 299 || status < 200){
                    throw new ExecutionException(new Throwable("PUT future failed with status - "+status));
                }
                this.isSentAlready = true;
            }
        }
        return true;
    }

    public RGridResource getResource() {
        return this.resource;
    }

    @Override
    public String toString() {
        return this.resource.getUrl();
    }
}
