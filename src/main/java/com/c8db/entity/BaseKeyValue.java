/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import com.c8db.entity.DocumentField.Type;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.c8db.entity.DocumentField.Type.EXPIRE_AT;
import static com.c8db.entity.DocumentField.Type.VALUE;

@Data
public class BaseKeyValue extends BaseDocument {

    @DocumentField(VALUE)
    protected Object value;

    @DocumentField(EXPIRE_AT)
    protected Long expireAt;

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
