/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

public interface StreamTransactionIdMixin<R> {

    String STREAM_TRANSACTION_ID = "streamTransactionId";

    <T> T getProperty(String name);

    <T> void setProperty(String name, T value);

    /**
     * @param streamTransactionId If set, the operation will be executed within the
     *                            transaction.
     * @return options
     */
    default R streamTransactionId(final String streamTransactionId) {
        setProperty(STREAM_TRANSACTION_ID, streamTransactionId);
        return (R) this;
    }

    /**
     * @return transaction id
     */
    default String getStreamTransactionId() {
        return getProperty(STREAM_TRANSACTION_ID);
    }

}
