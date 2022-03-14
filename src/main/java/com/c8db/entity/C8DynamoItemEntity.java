package com.c8db.entity;

import lombok.Data;

import java.util.Map;

@Data
public class C8DynamoItemEntity {
    Map<String,Object> ConsumedCapacity;
    Object Item;
}
