/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import com.c8db.C8DBException;
import com.c8db.Protocol;
import com.c8db.SecretProvider;
import com.c8db.internal.net.HostDescription;
import com.c8db.internal.util.RequestUtils;
import com.c8db.internal.util.ResponseUtils;
import com.c8db.util.C8Serialization;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Secret provider implementation which could communicate with a remote identity provider to retrieve required secrets.
 */
public class C8RemoteSecretProvider implements SecretProvider {

    private final boolean useSsl;
    private final Protocol contentType;
    private String username;
    private String email;
    private char[] password;
    private HostDescription authHost;
    private C8Serialization serialization;
    private CloseableHttpClient client;

    public C8RemoteSecretProvider(boolean useSsl, Protocol contentType) {
        this.useSsl = useSsl;
        this.contentType = contentType;
    }

    public void init(SecretProviderContext context) {
        this.username = context.getUsername();
        this.email = context.getEmail();
        this.password = password != null ? password : "".toCharArray();
        this.serialization = context.getSerialization();
        this.client = context.getClient();
        this.authHost = context.getHost();
    }

    /**
     * Retrieve a token by talking to auth endpoint of c8 service.
     *
     * @return a secret token in JWT format.
     */
    @Override
    public String fetchSecret() {
        String authUrl = RequestUtils.buildBaseUrl(authHost, useSsl) + "/_open/auth";
        Map<String, String> credentials = new HashMap<>();

        credentials.put("username", username);
        credentials.put("password", new String(password));
        credentials.put("email", email);
        final HttpRequestBase authHttpRequest = RequestUtils.buildHttpRequestBase(
            new Request(null, null, RequestType.POST, authUrl)
                .setBody(serialization.serialize(credentials)),
            authUrl, contentType);
        authHttpRequest.setHeader(HttpHeaders.USER_AGENT,
            "Mozilla/5.0 (compatible; C8DB-JavaDriver/1.1; +http://mt.orz.at/)");

        if (contentType == Protocol.HTTP_VPACK) {
            authHttpRequest.setHeader(HttpHeaders.ACCEPT, "application/x-velocypack");
        }

        try {
            Response authResponse = ResponseUtils.buildResponse(serialization,
                    client.execute(authHttpRequest), contentType);
            ResponseUtils.checkError(serialization, authResponse);
            return authResponse.getBody().get("jwt").getAsString();
        } catch (IOException e) {
            throw new C8DBException(e);
        }
    }
}
