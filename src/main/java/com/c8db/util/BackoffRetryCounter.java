/**
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db.util;

public interface BackoffRetryCounter {

    /**
     * Reset counter to initial position
     */
    void reset();

    /**
     * Check if retry possible
     */
    boolean canRetry();

    /**
     * Increment counter after retry
     */
    void increment();

    /**
     * @return time in milliseconds for current retry
     */
    long getTimeIntervalMillis();

    /**
     * @return time interval in human-readable format
     */
    String getTimeInterval();
}
