/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

public class C8KVPairCreateOptions {

    private Boolean waitForSync;

    public C8KVPairCreateOptions() {
        super();
    }

    public Boolean getWaitForSync() {
        return waitForSync;
    }

    /**
     * @param waitForSync Wait until document has been synced to disk.
     * @return options
     */
    public C8KVPairCreateOptions waitForSync(final Boolean waitForSync) {
        this.waitForSync = waitForSync;
        return this;
    }

}
