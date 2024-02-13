/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import com.c8db.Protocol;
import com.c8db.credentials.C8Credentials;
import com.c8db.internal.net.HostDescription;
import com.c8db.util.C8Serialization;
import lombok.Builder;
import lombok.Getter;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Context parameters sent to secret provider when initializing.
 * This information will be passed from the C84j internals to the secret provider implementation
 * during the initialization of the secret provider.
 */

@Getter
@Builder
public class SecretProviderContext {
    private boolean useSsl;
    private C8Credentials credentials;
    private HostDescription host;
    private C8Serialization serialization;
    private CloseableHttpClient client;
    private Protocol contentType;
}
