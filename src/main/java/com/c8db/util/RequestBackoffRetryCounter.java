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
    private static final int DEFAULT_MAX_SLEEP_TIME_SEC = 128;

    private final Request request;
    private final int maxSleepTimeSec;
    private int currentWaitTime;

    public RequestBackoffRetryCounter(Request request, Integer retryTimeout) {
        this.request = request;
        if (retryTimeout != null) {
            maxSleepTimeSec = retryTimeout / 1000 / 2;
        } else {
            maxSleepTimeSec = DEFAULT_MAX_SLEEP_TIME_SEC;
        }
        this.currentWaitTime = INITIAL_SLEEP_TIME_SEC;
    }

    public synchronized void reset() {
        currentWaitTime = INITIAL_SLEEP_TIME_SEC;
    }

    public synchronized boolean canRetry() {
        return request.isRetryEnabled() && currentWaitTime <= maxSleepTimeSec;
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
