/**
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db.util;

import com.c8db.velocystream.Request;

/**
 * Backoff Retry Counter to count when to retry next during reconnection
 */
public class RequestBackoffRetryCounter implements BackoffRetryCounter {

    public static final int INITIAL_SLEEP_TIME_SEC = 4;
    public static final int SLEEP_TIME_MULTIPLIER = 2;
    private static final int MAX_SLEEP_TIME_SEC = 128;

    private Request request;
    private int currentWaitTime;

    public RequestBackoffRetryCounter(Request request) {
        this.request = request;
        this.currentWaitTime = INITIAL_SLEEP_TIME_SEC;
    }

    public synchronized void reset() {
        currentWaitTime = INITIAL_SLEEP_TIME_SEC;
    }

    public synchronized boolean canRetry() {
        return request.isRetryEnabled() && currentWaitTime <= MAX_SLEEP_TIME_SEC;
    }

    public synchronized void increment() {
        currentWaitTime *= SLEEP_TIME_MULTIPLIER;
    }

    public long getTimeIntervalMillis() {
        return currentWaitTime * 1000L;
    }

    public String getTimeInterval() {
        return currentWaitTime + " seconds";
    }
}
