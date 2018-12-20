package com.applitools.eyes;

import com.applitools.eyes.visualGridClient.services.IResourceFuture;
import com.applitools.eyes.visualGridClient.model.RGridResource;
import com.applitools.utils.GeneralUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ResourceFuture implements IResourceFuture {

    private Future<Response> future;
    private String url;
    private Logger logger;
    private RGridResource rgResource;

    public ResourceFuture(Future<Response> future, String url, Logger logger) {
        this.future = future;
        this.url = url;
        this.logger = logger;
    }

    public ResourceFuture(RGridResource rgResource) {
        this.url = rgResource.getUrl();
        this.rgResource = rgResource;
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
        if (this.rgResource == null) {
            Response response = future.get();
            ByteArrayOutputStream outputStream = downloadFile(response);
            String contentType = Utils.getResponseContentType(response);
            rgResource = new RGridResource(url, contentType, outputStream.toByteArray(), logger);
        }
        return rgResource;
    }

    @Override
    public RGridResource get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (this.rgResource == null) {
            Response response = future.get(timeout, unit);
            ByteArrayOutputStream outputStream = downloadFile(response);
            String contentType = Utils.getResponseContentType(response);
            rgResource = new RGridResource(url, contentType, outputStream.toByteArray(), logger);
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
