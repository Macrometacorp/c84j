/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

public class C8KVCreateOptions {

    private String name;
    private Boolean stream;
    private Boolean enableShards;
    private Boolean waitForSync;
    private String[] shardKeys;

    public C8KVCreateOptions() {
        super();
    }

    protected String getName() {
        return name;
    }

    /**
     * @param name The name of the collection
     * @return options
     */
    protected C8KVCreateOptions name(final String name) {
        this.name = name;
        return this;
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
    public C8KVCreateOptions shardKeys(final String... shardKeys) {
        this.shardKeys = shardKeys;
        return this;
    }

    public Boolean hasStream() {
        return stream;
    }

    /**
     * @param stream If true an associated stream will be created
     * @return {@link C8KVCreateOptions}
     */
    public C8KVCreateOptions stream(final Boolean stream) {
        this.stream = stream;
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
     * @return {@link C8KVCreateOptions}
     */
    public C8KVCreateOptions enableShards(final Boolean enableShards) {
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
     * @return {@link C8KVCreateOptions}
     */
    public C8KVCreateOptions waitForSync(final Boolean waitForSync) {
        this.waitForSync = waitForSync;
        return this;
    }

}
