/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

import com.c8db.C8KeyValue;

public interface C8KVCreateMixin<R> {

    String STREAM_PARAMETER = "stream";
    String ENABLE_SHARDS_PARAMETER = "enableShards";
    String WAIT_FOR_SYNC_PARAMETER = "waitForSync";
    String BLOBS_PARAMETER = "blobs";
    String SHARD_KEYS_PARAMETER = "shardKeys";
    String EXPIRATION_PARAMETER = "expiration";
    String GROUP_PARAMETER = "group";

    <T> T getProperty(String name);

    <T> void setProperty(String name, T value);

    default String[] getShardKeys() {
        return getProperty(SHARD_KEYS_PARAMETER);
    }

    /**
     * @param shardKeys (The default is [ "_key" ]): in a cluster, this attribute
     *                  determines which document attributes are used to determine
     *                  the target shard for documents. Documents are sent to shards
     *                  based on the values of their shard key attributes. The
     *                  values of all shard key attributes in a document are hashed,
     *                  and the hash value is used to determine the target shard.
     *                  Note: Values of shard key attributes cannot be changed once
     *                  set. This option is meaningless in a single server setup.
     * @return options
     */
    default R shardKeys(final String... shardKeys) {
        setProperty(SHARD_KEYS_PARAMETER, shardKeys);
        return (R) this;
    }

    default boolean hasStream() {
        return getProperty(STREAM_PARAMETER) == Boolean.TRUE;
    }

    /**
     * @param stream If true an associated stream will be created
     * @return {@link C8KVCreateBodyOptions}
     */
    default R stream(final boolean stream) {
        setProperty(STREAM_PARAMETER, stream);
        return (R) this;
    }

    default boolean isBlobs() {
        return getProperty(BLOBS_PARAMETER) == Boolean.TRUE;
    }

    /**
     * @param blobs true if collection is a blob-only collection (default: false)
     * @return {@link C8KVCreateBodyOptions}
     */
    default R blobs(final boolean blobs) {
        setProperty(BLOBS_PARAMETER, blobs);
        return (R) this;
    }

    /**
     * Checks whether the shards are enabled.
     *
     * @return true of false
     */
    default boolean isEnableShards() {
        return getProperty(ENABLE_SHARDS_PARAMETER) == Boolean.TRUE;
    }

    /**
     * @param enableShards Sets numberOfShards to 8 if true else numberOfShards is set to 1
     * @return {@link C8KVCreateBodyOptions}
     */
    default R enableShards(final boolean enableShards) {
        setProperty(ENABLE_SHARDS_PARAMETER, enableShards);
        return (R) this;
    }

    /**
     * Checks whether the waitForSync is enabled.
     *
     * @return true of false
     */
    default boolean isWaitForSync() {
        return getProperty(WAIT_FOR_SYNC_PARAMETER) == Boolean.TRUE;
    }

    /**
     * Sets waitForSync collection creation property.
     *
     * @return {@link R}
     */
    default R waitForSync(final boolean waitForSync) {
        setProperty(WAIT_FOR_SYNC_PARAMETER, waitForSync);
        return (R) this;
    }

    default boolean hasExpiration() {
        return getProperty(EXPIRATION_PARAMETER) == Boolean.TRUE;
    }

    /**
     * @param expiration Enable TTL support (default: false)
     * @return {@link C8KVCreateOptions}
     */
    default R expiration(final boolean expiration) {
        setProperty(EXPIRATION_PARAMETER, expiration);
        return (R) this;
    }

    default boolean hasGroup() {
        return getProperty(GROUP_PARAMETER) == Boolean.TRUE;
    }

    /**
     * @param group Enable group support (default: false)
     * @return {@link C8KVCreateOptions}
     */
    default R group(final boolean group) {
        setProperty(GROUP_PARAMETER, group);
        return (R) this;
    }

}
