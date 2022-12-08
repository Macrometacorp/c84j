/**
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db;

import com.c8db.entity.FxEntity;
import com.c8db.entity.FxMetadataEntity;
import com.c8db.model.FxReadOptions;

import java.util.Collection;
import java.util.Map;

public interface C8Compute {

    /**
     * Fetches a list of all functions.
     *
     * @return
     * @throws C8DBException
     */
    Collection<FxEntity> getFunctions() throws C8DBException;

    /**
     * Fetches a list of all functions.
     *
     * @param options - filter by FxReadOptions instance
     * @return
     * @throws C8DBException
     */
    Collection<FxEntity> getFunctions(final FxReadOptions options) throws C8DBException;

    /**
     * Get information about function worker.
     *
     * @return - result as FxEntity object
     * @throws C8DBException
     */
    FxEntity getInfo(final String name) throws C8DBException;

    /**
     * Fetch information about a edge worker.
     *
     * @return - result as FxMetadataEntity object
     * @throws C8DBException
     */
    FxMetadataEntity getMetadata() throws C8DBException;

    /**
     * Execute a function worker.
     *
     * @param name - name of function
     * @param arguments - set parameters with arguments as a map
     * @return - result of execution of a function.
     * @throws C8DBException
     */
    Object executeFunction(String name, Map<String, Object> arguments) throws C8DBException;
}
