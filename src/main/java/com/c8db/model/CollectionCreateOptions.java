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

import com.c8db.entity.CollectionType;
import com.c8db.entity.KeyOptions;
import com.c8db.entity.KeyType;

/**
 */
public class CollectionCreateOptions {

    private String name;
    private KeyOptions keyOptions;
    private Boolean isSpot;
    private String[] shardKeys;
    private CollectionType type;
    

    public CollectionCreateOptions() {
        super();
    }

    protected String getName() {
        return name;
    }

    /**
     * @param name The name of the collection
     * @return options
     */
    protected CollectionCreateOptions name(final String name) {
        this.name = name;
        return this;
    }

    public KeyOptions getKeyOptions() {
        return keyOptions;
    }

    /**
     * @param allowUserKeys if set to true, then it is allowed to supply own key
     *                      values in the _key attribute of a document. If set to
     *                      false, then the key generator will solely be responsible
     *                      for generating keys and supplying own key values in the
     *                      _key attribute of documents is considered an error.
     * @param type          specifies the type of the key generator. The currently
     *                      available generators are traditional and autoincrement.
     * @param increment     increment value for autoincrement key generator. Not
     *                      used for other key generator types.
     * @param offset        Initial offset value for autoincrement key generator.
     *                      Not used for other key generator types.
     * @return options
     */
    public CollectionCreateOptions keyOptions(final Boolean allowUserKeys, final KeyType type, final Integer increment,
            final Integer offset) {
        this.keyOptions = new KeyOptions(allowUserKeys, type, increment, offset);
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
    public CollectionCreateOptions shardKeys(final String... shardKeys) {
        this.shardKeys = shardKeys;
        return this;
    }

    public CollectionType getType() {
        return type;
    }

    /**
     * @param type (The default is {@link CollectionType#DOCUMENT}): the type of the
     *             collection to create.
     * @return options
     */
    public CollectionCreateOptions type(final CollectionType type) {
        this.type = type;
        return this;
    }

    public Boolean getIsSpot() {
        return isSpot;
    }

    /**
     * @param isSpot If true then all access to the collection is done on the spot
     *               region of the fabric the collection is in. (default: false)
     * @return options
     */
    public CollectionCreateOptions isSpot(final Boolean isSpot) {
        this.isSpot = isSpot;
        return this;
    }

}
