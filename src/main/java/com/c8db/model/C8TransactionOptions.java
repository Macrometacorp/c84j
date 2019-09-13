/*
 * DISCLAIMER
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.c8db.model;

/**
 * @see <a href=
 *      "https://docs.arangodb.com/current/HTTP/Transaction/index.html#execute-transaction">API
 *      Documentation</a>
 */
public class C8TransactionOptions {

    private String action;
    private Object params;
    private final C8TransactionCollectionOptions collections;
    private Integer lockTimeout;
    private Boolean waitForSync;

    public C8TransactionOptions() {
        super();
        collections = new C8TransactionCollectionOptions();
    }

    protected String getAction() {
        return action;
    }

    /**
     * @param action the actual transaction operations to be executed, in the form
     *               of stringified JavaScript code
     * @return options
     */
    protected C8TransactionOptions action(final String action) {
        this.action = action;
        return this;
    }

    public Object getParams() {
        return params;
    }

    /**
     * @param params optional arguments passed to action
     * @return options
     */
    public C8TransactionOptions params(final Object params) {
        this.params = params;
        return this;
    }

    public Integer getLockTimeout() {
        return lockTimeout;
    }

    /**
     * @param lockTimeout an optional numeric value that can be used to set a
     *                    timeout for waiting on collection locks. If not specified,
     *                    a default value will be used. Setting lockTimeout to 0
     *                    will make ArangoDB not time out waiting for a lock.
     * @return options
     */
    public C8TransactionOptions lockTimeout(final Integer lockTimeout) {
        this.lockTimeout = lockTimeout;
        return this;
    }

    public Boolean getWaitForSync() {
        return waitForSync;
    }

    /**
     * @param waitForSync an optional boolean flag that, if set, will force the
     *                    transaction to write all data to disk before returning
     * @return options
     */
    public C8TransactionOptions waitForSync(final Boolean waitForSync) {
        this.waitForSync = waitForSync;
        return this;
    }

    /**
     * @param read contains the array of collection-names to be used in the
     *             transaction (mandatory) for read
     * @return options
     */
    public C8TransactionOptions readCollections(final String... read) {
        collections.read(read);
        return this;
    }

    /**
     * @param write contains the array of collection-names to be used in the
     *              transaction (mandatory) for write
     * @return options
     */
    public C8TransactionOptions writeCollections(final String... write) {
        collections.write(write);
        return this;
    }

    /**
     * @param exclusive contains the array of collection-names to be used in the
     *                  transaction (mandatory) for exclusive write
     * @return options
     * @since ArangoDB 3.4.0
     */
    public C8TransactionOptions exclusiveCollections(final String... exclusive) {
        collections.exclusive(exclusive);
        return this;
    }

    /**
     * @param allowImplicit Collections that will be written to in the transaction
     *                      must be declared with the write attribute or it will
     *                      fail, whereas non-declared collections from which is
     *                      solely read will be added lazily. The optional attribute
     *                      allowImplicit can be set to false to let transactions
     *                      fail in case of undeclared collections for reading.
     *                      Collections for reading should be fully declared if
     *                      possible, to avoid deadlocks.
     * @return options
     */
    public C8TransactionOptions allowImplicit(final Boolean allowImplicit) {
        collections.allowImplicit(allowImplicit);
        return this;
    }
}
