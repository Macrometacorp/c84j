/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import com.c8db.entity.DocumentField.Type;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class BaseKeyValue implements Serializable {

    private static final long serialVersionUID = -1824742667228719116L;

    @DocumentField(Type.ID)
    protected String id;
    @DocumentField(Type.KEY)
    protected String key;
    @DocumentField(Type.REV)
    protected String revision;
    @DocumentField(Type.VALUE)
    protected String value;
    @DocumentField(Type.EXPIRE_AT)
    protected long expireAt;

    public BaseKeyValue() {
        super();
    }

    public BaseKeyValue(final String key) {
        this();
        this.key = key;
    }

    public BaseKeyValue(final String value, long expireAt) {
        this();
        this.value = value;
        this.expireAt = expireAt;
    }
}
