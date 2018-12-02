package com.applitools.eyes;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PutFuture implements Future<Boolean> {

    private Future putFuture;

    private boolean isSentAlready = false;

    public PutFuture(Future putFuture) {
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
        if (!this.isSentAlready) {
            this.putFuture.get();
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
