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
    private String url;

    public ResourceFuture(Future<Response> future, String url) {
        this.future = future;
        this.url = url;
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
        byte[] bytes = new byte[response.getLength()];
        try {
            response.readEntity(InputStream.class).read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new RGridResource(url, response.getHeaderString("contentType"), ArrayUtils.toObject(bytes));
    }

    @Override
    public RGridResource get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Response response = future.get(timeout, unit);
        byte[] bytes = new byte[response.getLength()];
        try {
            response.readEntity(InputStream.class).read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new RGridResource(url, response.getHeaderString("contentType"), ArrayUtils.toObject(bytes));
    }

}
