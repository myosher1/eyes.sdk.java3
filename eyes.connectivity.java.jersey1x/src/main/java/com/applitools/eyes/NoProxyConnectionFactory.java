package com.applitools.eyes;

import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NoProxyConnectionFactory implements HttpURLConnectionFactory {

    @Override
    public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }
}
