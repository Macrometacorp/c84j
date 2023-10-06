/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

/**
 * Internal class
 */
public class C8KVCreateBodyOptions {

    private Boolean stream;
    private Boolean enableShards;
    private Boolean waitForSync;
    private Boolean blobs;
    private String[] shardKeys;
    private Boolean strongConsistency;

    public C8KVCreateBodyOptions() {
        super();
    }

    public String[] getShardKeys() {
        return shardKeys;
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
    public C8KVCreateBodyOptions shardKeys(final String... shardKeys) {
        this.shardKeys = shardKeys;
        return this;
    }

    public Boolean hasStream() {
        return stream;
    }

    /**
     * @param stream If true an associated stream will be created
     * @return {@link C8KVCreateBodyOptions}
     */
    public C8KVCreateBodyOptions stream(final Boolean stream) {
        this.stream = stream;
        return this;
    }

    public Boolean isBlobs() {
        return blobs;
    }

    /**
     * @param blobs true if collection is a blob-only collection (default: false)
     * @return {@link C8KVCreateBodyOptions}
     */
    public C8KVCreateBodyOptions blobs(final Boolean blobs) {
        this.blobs = blobs;
        return this;
    }

    /**
     * Checks whether the shards are enabled.
     *
     * @return true of false
     */
    public Boolean isEnableShards() {
        return enableShards;
    }

    /**
     * @param enableShards Sets numberOfShards to 8 if true else numberOfShards is set to 1
     * @return {@link C8KVCreateBodyOptions}
     */
    public C8KVCreateBodyOptions enableShards(final Boolean enableShards) {
        this.enableShards = enableShards;
        return this;
    }

    /**
     * Checks whether the waitForSync is enabled.
     *
     * @return true of false
     */
    public Boolean isWaitForSync() {
        return waitForSync;
    }

    /**
     * Sets waitForSync collection creation property.
     *
     * @return {@link C8KVCreateBodyOptions}
     */
    public C8KVCreateBodyOptions waitForSync(final Boolean waitForSync) {
        this.waitForSync = waitForSync;
        return this;
    }

    /**
     * Checks whether the strongConsistency is enabled.
     *
     * @return true of false
     */
    public Boolean isStrongConsistency() {
        return strongConsistency;
    }

    /**
     * Sets strongConsistency collection creation property.
     *
     * @return {@link C8KVCreateBodyOptions}
     */
    public C8KVCreateBodyOptions strongConsistency(final Boolean strongConsistency) {
        this.strongConsistency = strongConsistency;
        return this;
    }

}
