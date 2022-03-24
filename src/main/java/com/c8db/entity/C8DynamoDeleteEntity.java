/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */
package com.c8db.entity;

import lombok.Data;

@Data
public class C8DynamoDeleteEntity {
   C8DynamoTableDescription TableDescription;

    @Data
    public static class C8DynamoTableDescription {
        private String TableName;
        private String TableStatus;
        private String TableArn;
    }
}
