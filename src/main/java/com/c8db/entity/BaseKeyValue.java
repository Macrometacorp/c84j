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

@Data
public class BaseKeyValue implements Serializable {

    private static final long serialVersionUID = 2521004693545703973L;

    @DocumentField(Type.ID)
    protected String id;
    @DocumentField(Type.KEY)

    protected String key;
    @DocumentField(Type.REV)
    protected String revision;

    @DocumentField(Type.VALUE)
    @NonNull
    protected String value;

    @DocumentField(Type.EXPIRE_AT)
    @NonNull
    protected Long expireAt;

}
