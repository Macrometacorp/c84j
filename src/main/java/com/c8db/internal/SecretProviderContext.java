/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import com.c8db.internal.net.HostDescription;
import com.c8db.util.C8Serialization;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Context parameters sent to secret provider when initializing.
 * This information will be passed from the C84j internals to the secret provider implementation
 * during the initialization of the secret provider.
 */
public class SecretProviderContext {
    private String username;
    private String email;
    private char[] password;
    private HostDescription host;
    private C8Serialization serialization;
    private CloseableHttpClient client;

    private SecretProviderContext() {}

    public static class Builder {
        private String username;
        private String email;
        private char[] password;
        private HostDescription host;
        private C8Serialization serialization;
        private CloseableHttpClient client;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(char[] password) {
            this.password = password;
            return this;
        }

        public Builder host(HostDescription host) {
            this.host = host;
            return this;
        }

        public Builder serialization(C8Serialization serialization) {
            this.serialization = serialization;
            return this;
        }

        public Builder client(CloseableHttpClient client) {
            this.client = client;
            return this;
        }

        public SecretProviderContext build() {
            SecretProviderContext ctx = new SecretProviderContext();
            ctx.client = client;
            ctx.username = username;
            ctx.password = password;
            ctx.email = email;
            ctx.host = host;
            ctx.serialization = serialization;
            return ctx;
        }
    }

    public String getUsername() {
        return username;
    }

    public char[] getPassword() {
        return password;
    }

    public HostDescription getHost() {
        return host;
    }

    public C8Serialization getSerialization() {
        return serialization;
    }

    public CloseableHttpClient getClient() {
        return client;
    }

    public String getEmail() {
        return email;
    }

}
