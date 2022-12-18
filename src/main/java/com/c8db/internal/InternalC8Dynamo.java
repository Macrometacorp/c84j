/*
 *
 * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */
package com.c8db.internal;

import com.amazonaws.ImmutableRequest;
import com.amazonaws.protocol.json.JsonClientMetadata;
import com.amazonaws.protocol.json.SdkJsonProtocolFactory;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.transform.CreateTableRequestProtocolMarshaller;
import com.amazonaws.services.dynamodbv2.model.transform.DeleteItemRequestProtocolMarshaller;
import com.amazonaws.services.dynamodbv2.model.transform.DeleteTableRequestProtocolMarshaller;
import com.amazonaws.services.dynamodbv2.model.transform.DescribeTableRequestProtocolMarshaller;
import com.amazonaws.services.dynamodbv2.model.transform.GetItemRequestProtocolMarshaller;
import com.amazonaws.services.dynamodbv2.model.transform.PutItemRequestProtocolMarshaller;
import com.amazonaws.services.dynamodbv2.model.transform.ScanRequestProtocolMarshaller;
import com.amazonaws.services.dynamodbv2.model.transform.UpdateItemRequestProtocolMarshaller;
import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.C8DBException;
import com.c8db.entity.C8DynamoGetItemEntity;
import com.c8db.entity.C8DynamoItemWithAttributeValuesEntity;
import com.c8db.entity.C8DynamoItemsWithAttributeValuesEntity;
import com.c8db.entity.C8DynamoGetItemsEntity;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.internal.util.IOUtils;
import com.c8db.model.C8DynamoCreateTableOptions;
import com.c8db.model.C8DynamoGetItemsOptions;
import com.c8db.util.C8DynamoUtils;
import com.c8db.util.C8Serializer;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

import java.util.*;
import java.util.stream.Collectors;

public abstract class InternalC8Dynamo<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {
    protected static final String PATH_API_DYNAMO = "/_api/dynamo";
    public static final String C8_DYNAMO_HEADER_KEY = "X-Amz-Target";
    public static final String C8_DYNAMO_CREATE_TABLE_VAL = "DynamoDB_20120810.CreateTable";
    public static final String C8_DYNAMO_DELETE_TABLE_VAL = "DynamoDB_20120810.DeleteTable";
    public static final String C8_DYNAMO_DESCRIBE_TABLE_VAL = "DynamoDB_20120810.DescribeTable";
    public static final String C8_DYNAMO_GET_ITEM_VAL = "DynamoDB_20120810.GetItem";
    public static final String C8_DYNAMO_PUT_ITEM_VAL = "DynamoDB_20120810.PutItem";
    public static final String C8_DYNAMO_UPDATE_ITEM_VAL = "DynamoDB_20120810.UpdateItem";
    public static final String C8_DYNAMO_DELETE_ITEM_VAL = "DynamoDB_20120810.DeleteItem";
    public static final String C8_DYNAMO_GET_ITEMS_VAL = "DynamoDB_20120810.Scan";
    private final D db;
    protected volatile String tableName;
    protected SdkJsonProtocolFactory protocolFactory;

    public D db() {
        return db;
    }

    protected InternalC8Dynamo(final D db, final String tableName) {
        super(db.executor, db.util, db.context);
        this.db = db;
        this.tableName = tableName;
        this.protocolFactory = new SdkJsonProtocolFactory((new JsonClientMetadata()));
    }

    protected Request createTableRequest(final String tableName, final C8DynamoCreateTableOptions options) {
        List<KeySchemaElement> keySchema = options.getKeySchema().stream()
            .map(schema -> new KeySchemaElement(schema.getAttributeName(), schema.getKeyType().getKey()))
            .collect(Collectors.toList());
        List<AttributeDefinition> attributeDefinitions = options.getAttributeDefinitions().stream()
            .map(def -> new AttributeDefinition(def.getAttributeName(), def.getAttributeType().getKey()))
            .collect(Collectors.toList());
        CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName).withKeySchema(keySchema)
            .withAttributeDefinitions(attributeDefinitions);
        ImmutableRequest<CreateTableRequest> awsRequest = (new CreateTableRequestProtocolMarshaller(protocolFactory))
            .marshall(createTableRequest);
        final Request request = setRequestParams(awsRequest);
        request.putHeaderParam(C8_DYNAMO_HEADER_KEY, C8_DYNAMO_CREATE_TABLE_VAL);
        return request;
    }

    protected Request deleteTableRequest(final String tableName) {
        DeleteTableRequest deleteTableRequest = new DeleteTableRequest().withTableName(tableName);
        ImmutableRequest<DeleteTableRequest> awsRequest = (new DeleteTableRequestProtocolMarshaller(protocolFactory))
            .marshall(deleteTableRequest);
        Request request = setRequestParams(awsRequest);
        request.putHeaderParam(C8_DYNAMO_HEADER_KEY, C8_DYNAMO_DELETE_TABLE_VAL);
        return request;
    }

    protected Request createDescribeRequest(final String tableName) {
        DescribeTableRequest dscribeTableRequest = new DescribeTableRequest().withTableName(tableName);
        ImmutableRequest<DescribeTableRequest> awsRequest = (new DescribeTableRequestProtocolMarshaller(protocolFactory))
            .marshall(dscribeTableRequest);
        Request request = setRequestParams(awsRequest);
        request.putHeaderParam(C8_DYNAMO_HEADER_KEY, C8_DYNAMO_DESCRIBE_TABLE_VAL);
        return request;
    }

    protected Request createPutItemRequest(final Map<String, Object> value) {
        PutItemRequest putItemRequest = new PutItemRequest()
            .withTableName(tableName)
            .withItem(C8DynamoUtils.toDynamoItem(value));
        ImmutableRequest<PutItemRequest> awsRequest = new PutItemRequestProtocolMarshaller(protocolFactory)
            .marshall(putItemRequest);
        final Request request = setRequestParams(awsRequest);
        request.putHeaderParam(C8_DYNAMO_HEADER_KEY, C8_DYNAMO_PUT_ITEM_VAL);
        return request;
    }

    // TODO: Issue on C8DB side. "AttributeUpdates" forbidden. Instead uses `createPutItemRequest`
    protected Request createUpdateItemRequest(final Map<String, Object> key, final Map<String, Object> updateAttributes) {
        Map<String, AttributeValueUpdate> attributeUpdates = updateAttributes.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                new AttributeValueUpdate().withValue(ItemUtils.toAttributeValue(entry.getValue()))));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest().withTableName(tableName)
            .withKey(C8DynamoUtils.toDynamoItem(key));

        ImmutableRequest<UpdateItemRequest> awsRequest = new UpdateItemRequestProtocolMarshaller(protocolFactory)
            .marshall(updateItemRequest);
        final Request request = setRequestParams(awsRequest);
        request.putHeaderParam(C8_DYNAMO_HEADER_KEY, C8_DYNAMO_UPDATE_ITEM_VAL);
        return request;
    }

    protected Request getItemRequest(final Map<String, Object> key) {
        GetItemRequest getItemRequest = new GetItemRequest().withTableName(tableName)
            .withKey(C8DynamoUtils.toDynamoItem(key));
        ImmutableRequest<GetItemRequest> awsRequest = new GetItemRequestProtocolMarshaller(protocolFactory)
            .marshall(getItemRequest);
        final Request request = setRequestParams(awsRequest);
        request.putHeaderParam(C8_DYNAMO_HEADER_KEY, C8_DYNAMO_GET_ITEM_VAL);
        return request;
    }

    protected C8Executor.ResponseDeserializer<C8DynamoGetItemEntity> getC8DynamoGetItemResponseDeserializer() {
        return response -> {
            final VPackSlice result = response.getBody();
            C8DynamoItemWithAttributeValuesEntity entityWithAttributeValues =
                util().deserialize(result, new Type<C8DynamoItemWithAttributeValuesEntity>(){}.getType());
            C8DynamoGetItemEntity entity = new C8DynamoGetItemEntity();
            entity.setItem(C8DynamoUtils.toOriginalItem(entityWithAttributeValues.getItem()));
            entity.setConsumedCapacity(entityWithAttributeValues.getConsumedCapacity());
            return entity;
        };
    }

    protected Request getItemsRequest(final C8DynamoGetItemsOptions options) {
        ScanRequest scanRequest = new ScanRequest().withTableName(tableName)
            .withLimit(options.getLimit())
            .withExclusiveStartKey(C8DynamoUtils.toDynamoItem(options.getExclusiveStartKey()));
        ImmutableRequest<ScanRequest> awsRequest = new ScanRequestProtocolMarshaller(protocolFactory)
            .marshall(scanRequest);
        final Request request= setRequestParams(awsRequest);
        request.putHeaderParam(C8_DYNAMO_HEADER_KEY, C8_DYNAMO_GET_ITEMS_VAL);
        return request;
    }

    protected C8Executor.ResponseDeserializer<C8DynamoGetItemsEntity> getC8DynamoGetItemsResponseDeserializer() {
        return response -> {
            final VPackSlice result = response.getBody();
            C8DynamoItemsWithAttributeValuesEntity entityWithAttributeValues =
                util().deserialize(result, new Type<C8DynamoItemsWithAttributeValuesEntity>(){}.getType());
            C8DynamoGetItemsEntity entity = new C8DynamoGetItemsEntity();
            entity.setItems(C8DynamoUtils.toOriginalListOfItems(entityWithAttributeValues.getItems()));
            entity.setLastEvaluatedKey(C8DynamoUtils.toOriginalItem(entityWithAttributeValues.getLastEvaluatedKey()));
            entity.setScannedCount(entityWithAttributeValues.getScannedCount());
            entity.setCount(entityWithAttributeValues.getCount());
            return entity;
        };
    }

    protected Request deleteItemRequest(final Map<String, Object> key) {
        DeleteItemRequest deleteItemRequest = new DeleteItemRequest().withTableName(tableName)
            .withKey(C8DynamoUtils.toDynamoItem(key));
        ImmutableRequest<DeleteItemRequest> awsRequest = new DeleteItemRequestProtocolMarshaller(protocolFactory)
            .marshall(deleteItemRequest);
        final Request request = setRequestParams(awsRequest);
        request.putHeaderParam(C8_DYNAMO_HEADER_KEY, C8_DYNAMO_DELETE_ITEM_VAL);
        return request;
    }

    private Request setRequestParams(ImmutableRequest awsRequest) {
        try {
            final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_DYNAMO);
            String dynamoJson = IOUtils.toString(awsRequest.getContent());
            VPackSlice slice = util(C8SerializationFactory.Serializer.CUSTOM).serialize(dynamoJson,
                new C8Serializer.Options().serializeNullValues(false).stringAsJson(true));
            request.setBody(slice);
            return request;
        } catch (Exception e) {
            throw new C8DBException(e);
        }
    }
}
