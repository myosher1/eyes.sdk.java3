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

    private String url = null;
    private RGridResource resource;
    private Logger logger;

    public ResourceFuture(Future<Response> future, Logger logger) {
        this.future = future;
        this.logger = logger;
    }

    public ResourceFuture(RGridResource resource, Logger logger) {
        this.resource = resource;
        this.url = resource.getUrl();
        this.logger = logger;
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
        Response response = future.get();
        ByteArrayOutputStream outputStream = downloadFile(response);

        @SuppressWarnings("UnnecessaryLocalVariable")
        RGridResource gridResource = new RGridResource(url,
                (String) response.getMetadata().get("contentType").get(0),
                ArrayUtils.toObject(outputStream.toByteArray()));

        return gridResource;
    }

    @Override
    public RGridResource get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Response response = future.get(timeout, unit);
        ByteArrayOutputStream outputStream = downloadFile(response);

        @SuppressWarnings("UnnecessaryLocalVariable")
        RGridResource gridResource = new RGridResource(url,
                (String) response.getMetadata().get("contentType").get(0),
                ArrayUtils.toObject(outputStream.toByteArray()));

        return gridResource;
    }

    private ByteArrayOutputStream downloadFile(Response response) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        InputStream inputStream = (InputStream) response.getEntity();
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
        return url;
    }

}
