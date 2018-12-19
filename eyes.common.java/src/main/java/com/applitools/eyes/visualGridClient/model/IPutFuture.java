package com.applitools.eyes.visualGridClient.model;

import java.util.concurrent.Future;

public interface IPutFuture extends Future<Boolean> {

    RGridResource getResource();

}
