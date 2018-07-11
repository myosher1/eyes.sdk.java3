package com.applitools.eyes;

import java.net.URI;

/**
 * Encapsulates creation of a connectivity provider.
 */
public class ServerConnectorFactory {
    /***
     *
     * @param logger A logger instance.
     * @param serverUrl The URI of the Eyes server.
     */
    static IServerConnector create(Logger logger, URI serverUrl) {
        return new ServerConnector(logger, serverUrl);
    }
}
