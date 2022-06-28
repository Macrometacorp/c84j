/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved
 */

package com.c8db;

/**
 * Defines the interface that should be implemented by each secret provider for the driver. This provider should be able
 * to retrieve a secret to communicate with c8 APIs
 */
public interface SecretProvider {

    /**
     * Retrieves a secret to communicate with the server.
     *
     * @return a valid secrete
     */
    String fetchSecret();
}
