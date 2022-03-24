/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */
package com.c8db.entity;

import java.util.List;
import java.util.Map;

public class C8DynamoItemEntity extends DocumentEntity{
    Map<String, Object> ConsumedCapacity;
    Map<String, Object> Item;

    List<Map<String, Object>> Items;
    Map<String, Map<String, Object>> LastEvaluatedKey;
    Long ScannedCount;
    Long Count;

    public C8DynamoItemEntity() {
        super();
    }

    @Override
    public String toString() {
        return "C8DynamoItemEntity{" +
                "ConsumedCapacity=" + ConsumedCapacity +
                ", Item=" + Item +
                ", Items=" + Items +
                ", LastEvaluatedKey=" + LastEvaluatedKey +
                ", ScannedCount=" + ScannedCount +
                ", Count=" + Count +
                '}';
    }
}
