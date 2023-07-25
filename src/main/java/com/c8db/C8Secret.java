/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db;

import com.c8db.entity.C8SecretEntity;
import com.c8db.model.C8SecretOptions;

/**
 * Interface for operations with Secrets
 */
public interface C8Secret extends C8SerializationAccessor {

    /**
     * Create a Secret.
     *
     * @param secretOptions attributes of the secret.
     * @return The created secret.
     */
    C8SecretEntity create(final C8SecretOptions secretOptions);

    /**
     * Retrieve a given Secret.
     *
     * @param name Name of the secret.
     * @return The secret.
     */
    C8SecretEntity get(final String name);

    /**
     * Update a given Secret.
     *
     * @param secretOptions attributes of the secret.
     * @return The updated secret.
     */
    C8SecretEntity update(final C8SecretOptions secretOptions);

    /**
     * Delete a given Secret.
     *
     * @param name Name of the secret to be deleted.
     */
    void delete(final String name);

}
