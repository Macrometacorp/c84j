/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import com.arangodb.velocypack.annotations.Expose;
import lombok.Data;

import java.util.Map;

import static com.c8db.entity.DocumentField.Type.EXPIRE_AT;
import static com.c8db.entity.DocumentField.Type.GROUP_ID;
import static com.c8db.entity.DocumentField.Type.VALUE;

@Data
public class BlobKeyValue extends BaseDocument {

    @Expose(serialize = false, deserialize = false)
    protected byte[] value;

    @DocumentField(EXPIRE_AT)
    protected Long expireAt;

    @DocumentField(GROUP_ID)
    protected String groupID;

    public BlobKeyValue() {
        super();
    }

    public BlobKeyValue(final byte[] value, final Long expireAt) {
        super();
        this.value = value;
        this.expireAt = expireAt;
    }

    public BlobKeyValue(final String key, final byte[] value, final Long expireAt) {
        super(key);
        this.value = value;
        this.expireAt = expireAt;
    }

    public BlobKeyValue(final String key, final byte[] value, final Long expireAt, final String groupID) {
        super(key);
        this.value = value;
        this.expireAt = expireAt;
        this.groupID = groupID;
    }

    public BlobKeyValue(final Map<String, Object> properties) {
        super(properties);

        final Object tmpValue = properties.remove(VALUE.getSerializeName());
        if (tmpValue != null) {
            this.value = (byte[]) tmpValue;
        }

        final Object tmpExpireAt = properties.remove(EXPIRE_AT.getSerializeName());
        if (tmpExpireAt != null) {
            this.expireAt = (Long) tmpExpireAt;
        }

        final Object groupID = properties.remove(GROUP_ID.getSerializeName());
        if (groupID != null) {
            this.groupID = (String) groupID;
        }
    }

}
