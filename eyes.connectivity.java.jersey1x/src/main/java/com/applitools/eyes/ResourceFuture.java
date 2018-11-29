package com.applitools.eyes;

import com.applitools.eyes.visualGridClient.IResourceFuture;
import com.applitools.eyes.visualGridClient.data.RGridResource;
import org.apache.commons.lang3.ArrayUtils;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ResourceFuture implements IResourceFuture {

    private Future<Response> future;
    private String url = null;

    public ResourceFuture(Future<Response> future) {
        this.future = future;
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
        byte[] bytes;
        bytes = new byte[(Integer)(response.getMetadata().get("length").get(0))];
        try {
            ((InputStream) response.getEntity()).read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        RGridResource gridResource = new RGridResource(url, (String) response.getMetadata().get("contentType").get(0), ArrayUtils.toObject(bytes));
        return gridResource;
    }

    @Override
    public RGridResource get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Response response = future.get(timeout, unit);
        byte[] bytes;
        bytes = new byte[(Integer)(response.getMetadata().get("length").get(0))];
        try {
            ((InputStream) response.getEntity()).read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        RGridResource gridResource = new RGridResource(url, (String) response.getMetadata().get("contentType").get(0), ArrayUtils.toObject(bytes));
        return gridResource;
    }

}
