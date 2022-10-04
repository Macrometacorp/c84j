/**
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.internal.http;

import org.apache.http.MessageConstraintException;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;

import javax.net.ssl.SSLException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class HttpRequestRetryHandler extends DefaultHttpRequestRetryHandler {

    public HttpRequestRetryHandler() {
        super(3, false,
            Arrays.asList(InterruptedIOException.class, UnknownHostException.class,
                ConnectException.class, SSLException.class, MessageConstraintException.class));
    }
}
