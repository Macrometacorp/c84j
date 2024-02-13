/*
 * Copyright (c) 2022 - 2024 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import com.c8db.C8DBException;
import com.c8db.Protocol;
import com.c8db.SecretProvider;
import com.c8db.credentials.C8Credentials;
import com.c8db.credentials.DefaultCredentials;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Secret provider implementation which could communicate with a remote identity provider to retrieve required secrets.
 */
public class C8RemoteSecretProvider implements SecretProvider {
    private Protocol contentType;
    private boolean useSsl;
    private C8Credentials credentials;
    private HostDescription authHost;
    private C8Serialization serialization;
    private CloseableHttpClient client;

    public void init(SecretProviderContext context) {
        this.credentials = context.getCredentials();
        this.serialization = context.getSerialization();
        this.client = context.getClient();
        this.authHost = context.getHost();
        this.useSsl = context.isUseSsl();
        this.contentType = context.getContentType();
    }

    /**
     * Retrieve a token by talking to auth endpoint of c8 service.
     *
     * @param tenant not used
     * @return a secret token in JWT format.
     */
    @Override
    public String fetchSecret(String tenant, String user) {
        String authUrl = RequestUtils.buildBaseUrl(authHost, useSsl) + "/_open/auth";
        Map<String, String> credentialsMap = new HashMap<>();

        if (credentials instanceof DefaultCredentials) {
            DefaultCredentials defaultCredentials = (DefaultCredentials) credentials;
            credentialsMap.put("username", defaultCredentials.getUser());
            credentialsMap.put("password", defaultCredentials.getPassword());
            credentialsMap.put("email", defaultCredentials.getEmail());
            final HttpRequestBase authHttpRequest = RequestUtils.buildHttpRequestBase(
                            new Request(tenant, null, null, RequestType.POST, authUrl)
                            .setBody(serialization.serialize(credentialsMap)), authUrl, contentType);
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
        } else {
            throw new C8DBException("Support only DefaultCredentials");
        }
    }
}
