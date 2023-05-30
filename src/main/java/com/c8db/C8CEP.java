/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db;

import com.c8db.entity.C8StreamWorkerEntity;
import com.c8db.model.C8StreamWorkerOptions;

/**
 * Interface for operations on a Stream Worker.
 */
public interface C8CEP extends C8SerializationAccessor {

    /**
     * Create a Stream Worker.
     *
     * @param streamWorkerOptions attributes of the stream worker.
     * @return The created stream worker.
     */
    C8StreamWorkerEntity create(final C8StreamWorkerOptions streamWorkerOptions);

    /**
     * Retrieve a given Stream Worker.
     *
     * @param streamWorkerName Name of the stream worker.
     * @return The stream worker.
     */
    C8StreamWorkerEntity get(final String streamWorkerName);

    /**
     * Retrieve a given Stream Worker on behalf of a different user.
     * Note: This can be invoked only by _mm or service user.
     *
     * @param streamWorkerName Name of the stream worker.
     * @param onBehalfOfUser   Username in "tenant.user" format.
     * @param isSystem         Whether the stream worker is a system stream worker.
     * @return The stream worker.
     */
    C8StreamWorkerEntity get(final String streamWorkerName, final String onBehalfOfUser, final boolean isSystem);

    /**
     * Update a given Stream Worker.
     *
     * @param streamWorkerName    Name of the stream worker to be updated.
     * @param streamWorkerOptions attributes of the stream worker.
     * @return The updated stream worker.
     */
    C8StreamWorkerEntity update(final String streamWorkerName, final C8StreamWorkerOptions streamWorkerOptions);

    /**
     * Update a given Stream Worker on behalf of a different user.
     * Note: This can be invoked only by _mm or service user.
     *
     * @param streamWorkerName    Name of the stream worker to be updated.
     * @param streamWorkerOptions attributes of the stream worker.
     * @param onBehalfOfUser      Username in "tenant.user" format.
     * @param isSystem            Whether the stream worker is a system stream worker.
     * @return The updated stream worker.
     */
    C8StreamWorkerEntity update(final String streamWorkerName, final C8StreamWorkerOptions streamWorkerOptions,
                                final String onBehalfOfUser, final boolean isSystem);

    /**
     * Delete a given Stream Worker.
     *
     * @param streamWorkerName Name of the stream worker to be deleted.
     */
    void delete(final String streamWorkerName);

    /**
     * Delete a given Stream Worker on behalf of a different user.
     * Note: This can be invoked only by _mm or service user.
     *
     * @param streamWorkerName Name of the stream worker to be deleted.
     * @param onBehalfOfUser   Username in "tenant.user" format.
     * @param isSystem         Whether the stream worker is a system stream worker.
     */
    void delete(final String streamWorkerName, final String onBehalfOfUser, final boolean isSystem);

    /**
     * Activate/Deactivate a given Stream Worker.
     *
     * @param streamWorkerName of the stream worker to be switched.
     * @param isActive         new state of the stream worker.
     * @return The updated stream worker.
     */
    C8StreamWorkerEntity activate(final String streamWorkerName, final Boolean isActive);

    /**
     * Activate/Deactivate a given Stream Worker on behalf of a different user.
     * Note: This can be invoked only by _mm or service user.
     *
     * @param streamWorkerName of the stream worker to be switched.
     * @param isActive         new state of the stream worker.
     * @param onBehalfOfUser   Username in "tenant.user" format.
     * @param isSystem         Whether the stream worker is a system stream worker.
     * @return The updated stream worker.
     */
    C8StreamWorkerEntity activate(final String streamWorkerName, final Boolean isActive,
                                  final String onBehalfOfUser, final boolean isSystem);

}
