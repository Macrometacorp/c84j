/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */

package com.c8db.internal;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.AlertEntity;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.util.C8Serializer;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public abstract class InternalC8Alerts<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {

    protected static final String PATH_ALERT = "/_api/alerts";
    private final D db;

    protected InternalC8Alerts(final D db) {
        super(db.executor, db.util, db.context);
        this.db = db;
    }

    public D db() {
        return db;
    }

    protected Request getAlertRequest(final Map<String, String> queryParamMap) {
        Request request = request(null, null, RequestType.GET, PATH_ALERT);
        queryParamMap.forEach((key, value) -> request.putQueryParam(key, value));
        return request;
    }

    protected Request updateAlertRequest(String updateParam,  Map<String, String> queryParamMap) {
        Request request = request(null, null, RequestType.PUT, PATH_ALERT, updateParam);
        queryParamMap.forEach((key, value) -> request.putQueryParam(key, value));
        return request;
    }

    protected Request createAlertRequest(AlertEntity entity) {
        final Request request = request(null, null, RequestType.POST, PATH_ALERT);
        request.setBody(util(C8SerializationFactory.Serializer.CUSTOM).serialize(entity, new C8Serializer.Options()));
        return request;
    }

    protected C8Executor.ResponseDeserializer<Collection<AlertEntity>> alertsListResponseDeserializer() {
        return new C8Executor.ResponseDeserializer<Collection<AlertEntity>>() {
            @Override
            public Collection<AlertEntity> deserialize(final Response response) throws VPackException {
                Collection<AlertEntity> alertList = new ArrayList<>();
                for (Iterator<VPackSlice> iterator = response.getBody().arrayIterator(); iterator.hasNext();) {
                    alertList.add(util().deserialize(iterator.next(), new Type<AlertEntity>() {}.getType()));
                }
                return alertList;
            }
        };
    }

    protected C8Executor.ResponseDeserializer<AlertEntity> alertsResponseDeserializer() {
        return new C8Executor.ResponseDeserializer<AlertEntity>() {
            @Override
            public AlertEntity deserialize(final Response response) throws VPackException {
                AlertEntity entity = util().deserialize(response.getBody(), new Type<AlertEntity>() {
                }.getType());
                return entity;
            }
        };
    }
}
