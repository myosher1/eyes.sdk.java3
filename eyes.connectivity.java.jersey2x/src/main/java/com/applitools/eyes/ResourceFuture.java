package com.applitools.eyes;

import com.applitools.eyes.visualGridClient.IResourceFuture;
import com.applitools.eyes.visualGridClient.data.RGridResource;
import com.applitools.utils.GeneralUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
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

            byte[] bytes;
            InputStream inputStream = response.readEntity(InputStream.class);
            int available = response.getLength();

            if (available < 1) {
                try {
                    available = inputStream.available();
                } catch (IOException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
            }
            bytes = new byte[available];
            try {
                inputStream.read(bytes);
            } catch (IOException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }

            String contentType = Utils.getResponseContentType(response);

            rgResource = new RGridResource(url, contentType, ArrayUtils.toObject(bytes));
        }
        return rgResource;
    }


    @Override
    public RGridResource get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (this.rgResource == null) {
            Response response = future.get(timeout, unit);
            byte[] bytes = new byte[response.getLength()];
            try {
                response.readEntity(InputStream.class).read(bytes);
                rgResource = new RGridResource(url, response.getHeaderString("contentType"), ArrayUtils.toObject(bytes));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rgResource;
    }

    @Override
    public String getUrl() {
        return this.url;
    }
}
