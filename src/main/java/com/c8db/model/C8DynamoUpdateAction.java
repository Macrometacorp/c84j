/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */
package com.c8db.model;

import com.c8db.entity.C8DynamoSecondaryIndex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class C8DynamoUpdateAction {

    private C8DynamoUpdateType type;
    private C8DynamoSecondaryIndex index;

    public enum C8DynamoUpdateType {

        CREATE,
        DELETE

    }
}


