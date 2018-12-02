package com.applitools.eyes;

import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PutFuture implements Future<Boolean> {

    private Future<Response> putFuture;

    private boolean isSentAlready = false;

    public PutFuture(Future<Response> putFuture) {
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
    public Boolean get() throws InterruptedException, ExecutionException {
        Response response;
        if (!this.isSentAlready) {
            response = this.putFuture.get();
        }
        this.isSentAlready = true;
        return true;
    }

    @Override
    public Boolean get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (!this.isSentAlready) {
            this.putFuture.get(timeout, unit);
        }
        this.isSentAlready = true;
        return true;
    }
}
