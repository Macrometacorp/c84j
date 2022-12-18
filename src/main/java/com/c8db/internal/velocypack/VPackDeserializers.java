/*
 * DISCLAIMER
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.c8db.internal.velocypack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.c8db.entity.FxEntity;
import com.c8db.entity.FxType;
import com.c8db.model.C8DynamoAttributeType;
import com.c8db.model.C8DynamoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.velocypack.VPackDeserializationContext;
import com.arangodb.velocypack.VPackDeserializer;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.BaseDocument;
import com.c8db.entity.BaseEdgeDocument;
import com.c8db.entity.CollectionStatus;
import com.c8db.entity.CollectionType;
import com.c8db.entity.License;
import com.c8db.entity.LogLevel;
import com.c8db.entity.MinReplicationFactor;
import com.c8db.entity.Permissions;
import com.c8db.entity.QueryExecutionState;
import com.c8db.entity.ReplicationFactor;
import com.c8db.velocystream.Response;

/**
 *
 */
public class VPackDeserializers {

    private static final Logger LOGGER = LoggerFactory.getLogger(VPackDeserializers.class);
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final VPackDeserializer<Response> RESPONSE = new VPackDeserializer<Response>() {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public Response deserialize(final VPackSlice parent, final VPackSlice vpack,
                final VPackDeserializationContext context) throws VPackException {
            final Response response = new Response();
            response.setVersion(vpack.get(0).getAsInt());
            response.setType(vpack.get(1).getAsInt());
            response.setResponseCode(vpack.get(2).getAsInt());
            if (vpack.size() > 3) {
                response.setMeta((Map) context.deserialize(vpack.get(3), Map.class));
            }
            return response;
        }
    };

    public static final VPackDeserializer<CollectionType> COLLECTION_TYPE = new VPackDeserializer<CollectionType>() {
        @Override
        public CollectionType deserialize(final VPackSlice parent, final VPackSlice vpack,
                final VPackDeserializationContext context) throws VPackException {
            return CollectionType.fromType(vpack.getAsInt());
        }
    };

    public static final VPackDeserializer<C8DynamoType> C8_DYNAMO_TYPE = new VPackDeserializer<C8DynamoType>() {
        @Override
        public C8DynamoType deserialize(final VPackSlice parent, final VPackSlice vpack,
                                          final VPackDeserializationContext context) throws VPackException {
            return C8DynamoType.fromKey(vpack.getAsString());
        }
    };

    public static final VPackDeserializer<C8DynamoAttributeType> C8_DYNAMO_ATTRIBUTE_TYPE = new VPackDeserializer<C8DynamoAttributeType>() {
        @Override
        public C8DynamoAttributeType deserialize(final VPackSlice parent, final VPackSlice vpack,
                                        final VPackDeserializationContext context) throws VPackException {
            return C8DynamoAttributeType.fromKey(vpack.getAsString());
        }
    };

    public static final VPackDeserializer<C8DynamoAttributeType> C8_DYNAMO_ATTRIBUTE_VALUE = new VPackDeserializer<C8DynamoAttributeType>() {
        @Override
        public C8DynamoAttributeType deserialize(final VPackSlice parent, final VPackSlice vpack,
                                                 final VPackDeserializationContext context) throws VPackException {
            return C8DynamoAttributeType.fromKey(vpack.getAsString());
        }
    };

    public static final VPackDeserializer<CollectionStatus> COLLECTION_STATUS = new VPackDeserializer<CollectionStatus>() {
        @Override
        public CollectionStatus deserialize(final VPackSlice parent, final VPackSlice vpack,
                final VPackDeserializationContext context) throws VPackException {
            return CollectionStatus.fromStatus(vpack.getAsInt());
        }
    };

    @SuppressWarnings("unchecked")
    public static final VPackDeserializer<BaseDocument> BASE_DOCUMENT = new VPackDeserializer<BaseDocument>() {
        @SuppressWarnings("rawtypes")
        @Override
        public BaseDocument deserialize(final VPackSlice parent, final VPackSlice vpack,
                final VPackDeserializationContext context) throws VPackException {
            return new BaseDocument((Map) context.deserialize(vpack, Map.class));
        }
    };

    @SuppressWarnings("unchecked")
    public static final VPackDeserializer<BaseEdgeDocument> BASE_EDGE_DOCUMENT = new VPackDeserializer<BaseEdgeDocument>() {
        @SuppressWarnings("rawtypes")
        @Override
        public BaseEdgeDocument deserialize(final VPackSlice parent, final VPackSlice vpack,
                final VPackDeserializationContext context) throws VPackException {
            return new BaseEdgeDocument((Map) context.deserialize(vpack, Map.class));
        }
    };

    public static final VPackDeserializer<Date> DATE_STRING = new VPackDeserializer<Date>() {
        @Override
        public Date deserialize(final VPackSlice parent, final VPackSlice vpack,
                final VPackDeserializationContext context) throws VPackException {
            try {
                return new SimpleDateFormat(DATE_TIME_FORMAT).parse(vpack.getAsString());
            } catch (final ParseException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("got ParseException for date string: " + vpack.getAsString());
                }
            }
            return null;
        }
    };

    public static final VPackDeserializer<LogLevel> LOG_LEVEL = new VPackDeserializer<LogLevel>() {
        @Override
        public LogLevel deserialize(final VPackSlice parent, final VPackSlice vpack,
                final VPackDeserializationContext context) throws VPackException {
            return LogLevel.fromLevel(vpack.getAsInt());
        }
    };

    public static final VPackDeserializer<License> LICENSE = new VPackDeserializer<License>() {
        @Override
        public License deserialize(final VPackSlice parent, final VPackSlice vpack,
                final VPackDeserializationContext context) throws VPackException {

            return License.valueOf(vpack.getAsString().toUpperCase());
        }
    };

    public static final VPackDeserializer<Permissions> PERMISSIONS = new VPackDeserializer<Permissions>() {
        @Override
        public Permissions deserialize(final VPackSlice parent, final VPackSlice vpack,
                final VPackDeserializationContext context) throws VPackException {
            return Permissions.valueOf(vpack.getAsString().toUpperCase());
        }
    };

    public static final VPackDeserializer<FxType> FX_TYPE = new VPackDeserializer<FxType>() {
        @Override
        public FxType deserialize(final VPackSlice parent, final VPackSlice vpack,
                                       final VPackDeserializationContext context) throws VPackException {
            return FxType.valueOf(vpack.getAsString().toUpperCase());
        }
    };

    public static final VPackDeserializer<QueryExecutionState> QUERY_EXECUTION_STATE = new VPackDeserializer<QueryExecutionState>() {
        @Override
        public QueryExecutionState deserialize(final VPackSlice parent, final VPackSlice vpack,
                final VPackDeserializationContext context) throws VPackException {
            return QueryExecutionState.valueOf(vpack.getAsString().toUpperCase().replaceAll(" ", "_"));
        }
    };

    public static final VPackDeserializer<ReplicationFactor> REPLICATION_FACTOR = new VPackDeserializer<ReplicationFactor>() {
        @Override
        public ReplicationFactor deserialize(final VPackSlice parent, final VPackSlice vpack,
                final VPackDeserializationContext context) throws VPackException {
            final ReplicationFactor replicationFactor = new ReplicationFactor();
            if (vpack.isString() && vpack.getAsString().equals("satellite")) {
                replicationFactor.setSatellite(true);
            } else {
                replicationFactor.setReplicationFactor(vpack.getAsInt());
            }
            return replicationFactor;
        }
    };

    public static final VPackDeserializer<MinReplicationFactor> MIN_REPLICATION_FACTOR = new VPackDeserializer<MinReplicationFactor>() {
        @Override
        public MinReplicationFactor deserialize(final VPackSlice parent, final VPackSlice vpack,
                final VPackDeserializationContext context) throws VPackException {
            final MinReplicationFactor minReplicationFactor = new MinReplicationFactor();
            minReplicationFactor.setMinReplicationFactor(vpack.getAsInt());
            return minReplicationFactor;
        }
    };
}
