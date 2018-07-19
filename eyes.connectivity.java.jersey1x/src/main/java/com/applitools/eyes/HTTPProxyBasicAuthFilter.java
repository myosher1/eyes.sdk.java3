package com.applitools.eyes;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.core.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public final class HTTPProxyBasicAuthFilter extends ClientFilter {

    private final String authentication;

    private static final Charset CHARACTER_SET = Charset.forName("iso-8859-1");
    private static final String PROXY_AUTHORIZATION = "Proxy-Authorization";

    /**
     * Creates a new HTTP Basic Authentication filter using provided username
     * and password credentials. This constructor allows you to avoid storing
     * plain password value in a String variable.
     * @param username
     * @param password
     */
    public HTTPProxyBasicAuthFilter(final String username, final byte[] password) {
        try {

            final byte[] prefix = (username + ":").getBytes(CHARACTER_SET);
            final byte[] usernamePassword = new byte[prefix.length + password.length];

            System.arraycopy(prefix, 0, usernamePassword, 0, prefix.length);
            System.arraycopy(password, 0, usernamePassword, prefix.length, password.length);

            authentication = "Basic " + new String(Base64.encode(usernamePassword), "ASCII");
        } catch (UnsupportedEncodingException ex) {
            // This should never occur
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates a new HTTP Basic Authentication filter using provided username
     * and password credentials.
     * @param username
     * @param password
     */
    public HTTPProxyBasicAuthFilter(final String username, final String password) {
        this(username, password.getBytes(CHARACTER_SET));
    }

    @Override
    public ClientResponse handle(final ClientRequest cr) throws ClientHandlerException {

        if (!cr.getHeaders().containsKey(PROXY_AUTHORIZATION)) {
            cr.getHeaders().add(PROXY_AUTHORIZATION, authentication);
        }
        return getNext().handle(cr);
    }
}