package com.applitools.eyes;

import com.applitools.eyes.visualgridclient.services.IResourceFuture;
import com.applitools.eyes.visualgridclient.model.RGridResource;
import com.applitools.utils.GeneralUtils;

import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ResourceFuture implements IResourceFuture {

    private Future<Response> future;
    private String url;
    private Logger logger;
    private IServerConnector serverConnector;
    private RGridResource rgResource;
    private int retryCount = 3;

    public ResourceFuture(Future<Response> future, String url, Logger logger, IServerConnector serverConnector) {
        this.future = future;
        this.url = url;
        this.logger = logger;
        this.serverConnector = serverConnector;
    }

    public ResourceFuture(RGridResource rgResource, Logger logger, IServerConnector serverConnector) {
        this.url = rgResource.getUrl();
        this.rgResource = rgResource;
        this.logger = logger;
        this.serverConnector = serverConnector;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public RGridResource get() throws InterruptedException, ExecutionException {
        logger.verbose("enter - this.rgResource: " + this.rgResource);
        while (this.rgResource == null && retryCount > 0) {
            try {
                Response response = future.get();
                logger.verbose("response: " + response);
                ByteArrayOutputStream outputStream = downloadFile(response);
                String contentType = Utils.getResponseContentType(response);
                rgResource = new RGridResource(url, contentType, outputStream.toByteArray(), logger, "ResourceFuture");
                break;
            } catch (InterruptedException | ExecutionException e) {
                retryCount--;
                logger.verbose("Entering retry");
                try {
                    Thread.sleep(300);
                    IResourceFuture newFuture = serverConnector.downloadResource(new URL(this.url), true, null);
                    this.future = ((ResourceFuture) newFuture).future;
                } catch (MalformedURLException malformedUrlException) {
                    GeneralUtils.logExceptionStackTrace(logger, malformedUrlException);
                }
            }
        }
        logger.verbose("exit");
        return rgResource;
    }

    @Override
    public RGridResource get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (this.rgResource == null) {
            Response response = future.get(timeout, unit);
            ByteArrayOutputStream outputStream = downloadFile(response);
            String contentType = Utils.getResponseContentType(response);
            rgResource = new RGridResource(url, contentType, outputStream.toByteArray(), logger, "resourceFuture");
        }
        return rgResource;
    }

    private ByteArrayOutputStream downloadFile(Response response) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        InputStream inputStream = response.readEntity(InputStream.class);
        byte[] bytes = new byte[1024];
        try {
            int readBytes = inputStream.read(bytes);
            while (readBytes > 0) {
                outputStream.write(bytes, 0, readBytes);
                readBytes = inputStream.read(bytes);
            }
        } catch (IOException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return outputStream;
    }

    @Override
    public String getUrl() {
        return this.url;
    }
}
