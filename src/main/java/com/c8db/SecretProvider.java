/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved
 */

package com.c8db;

import com.c8db.internal.SecretProviderContext;

/**
 * Defines the interface that should be implemented by each secret provider for the driver. This provider should be able
 * to retrieve a secret to communicate with c8 APIs
 */
public interface SecretProvider {

    /**
     * Initializes the secret provider.
     *
     * @param context context variables for the initialization
     */
    void init(SecretProviderContext context);

    /**
     * Retrieves a secret to communicate with the server.
     *
     * @param tenant for which tenant to fetch the secret
     * @param user   for which user to fetch the secret
     * @return a valid secrete
     */
    String fetchSecret(String tenant, String user);
}
