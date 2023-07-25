/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import lombok.Data;

import java.util.Map;

import static com.c8db.entity.DocumentField.Type.*;

@Data
public class BaseKeyValue extends BaseDocument {

    @DocumentField(VALUE)
    protected Object value;

    @DocumentField(EXPIRE_AT)
    protected Long expireAt;

    @DocumentField(GROUP_ID)
    protected String groupID;

    public BaseKeyValue() {
        super();
    }

    public BaseKeyValue(final String value, final Long expireAt) {
        super();
        this.value = value;
        this.expireAt = expireAt;
    }

    public BaseKeyValue(final String key, final String value, final Long expireAt) {
        super(key);
        this.value = value;
        this.expireAt = expireAt;
    }

    public BaseKeyValue(final String key, final String value, final Long expireAt, final String groupID) {
        super(key);
        this.value = value;
        this.expireAt = expireAt;
        this.groupID = groupID;
    }

    public BaseKeyValue(final Map<String, Object> properties) {
        super(properties);

        final Object tmpValue = properties.remove(VALUE.getSerializeName());
        if (tmpValue != null) {
            this.value = tmpValue;
        }

        final Object tmpExpireAt = properties.remove(EXPIRE_AT.getSerializeName());
        if (tmpExpireAt != null) {
            this.expireAt = (Long) tmpExpireAt;
        }
    }
}
