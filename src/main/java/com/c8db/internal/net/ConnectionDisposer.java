/**
 * Copyright (c) 2024 Macrometa Corp All rights reserved.
 */
package com.c8db.internal.net;

interface ConnectionDisposer {

    /**
     * Return connection back to the connection pool
     */
    void dispose(Connection connection);
}
