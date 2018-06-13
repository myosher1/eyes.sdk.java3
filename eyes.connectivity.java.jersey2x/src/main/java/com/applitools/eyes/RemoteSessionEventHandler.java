package com.applitools.eyes;

import com.applitools.eyes.events.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

public class RemoteSessionEventHandler extends RestClient implements ISessionEventHandler {

    private String autSessionId;
    private String accessKey;

    private static final String SERVER_SUFFIX = "/applitools/sessions";

    public RemoteSessionEventHandler(Logger logger, URI serverUrl, String accessKey, String autSessionId, int timeout) {
        super(logger, serverUrl, timeout);
        this.autSessionId = autSessionId;
        this.accessKey = accessKey;
    }

    public RemoteSessionEventHandler(Logger logger, URI serverUrl, String autSessionId, String accessKey) {
        this(logger, serverUrl, autSessionId, accessKey,30*1000);
    }

    @Override
    public void initStarted() {
        Response response;

        try {
            response = endPoint.path(SERVER_SUFFIX).path(autSessionId)
                    .queryParam("accessKey", accessKey).
                    request(MediaType.APPLICATION_JSON).
                    put(Entity.json("{action: 'initStart'}"));
        } catch (RuntimeException e) {
            logger.log("Server request failed: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void initEnded() {

    }

    @Override
    public void setSizeWillStart(RectangleSize sizeToSet) {

    }

    @Override
    public void setSizeEnded() {

    }

    @Override
    public void testStarted(String autSessionId) {

    }

    @Override
    public void testEnded(String autSessionId, TestResults testResults) {

    }

    @Override
    public void validationWillStart(String autSessionId, ValidationInfo validationInfo) {

    }

    @Override
    public void validationEnded(String autSessionId, String validationId, ValidationResult validationResult) {

    }
}
