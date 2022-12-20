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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.arangodb.velocypack.VPackBuilder;
import com.arangodb.velocypack.VPackSerializationContext;
import com.arangodb.velocypack.VPackSerializer;
import com.arangodb.velocypack.ValueType;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.BaseDocument;
import com.c8db.entity.BaseEdgeDocument;
import com.c8db.entity.CollectionType;
import com.c8db.entity.DocumentField;
import com.c8db.entity.LogLevel;
import com.c8db.entity.MinReplicationFactor;
import com.c8db.entity.Permissions;
import com.c8db.entity.ReplicationFactor;
import com.c8db.internal.velocystream.internal.AuthenticationRequest;
import com.c8db.model.C8DynamoAttributeType;
import com.c8db.model.C8DynamoType;
import com.c8db.model.TraversalOptions;
import com.c8db.model.TraversalOptions.Order;
import com.c8db.velocystream.Request;

/**
 * 
 */
public class VPackSerializers {

    public static final VPackSerializer<Request> REQUEST = new VPackSerializer<Request>() {
        @Override
        public void serialize(
                final VPackBuilder builder,
                final String attribute,
                final Request value,
                final VPackSerializationContext context) throws VPackException {
            builder.add(attribute, ValueType.ARRAY);
            builder.add(value.getVersion());
            builder.add(value.getType());
            builder.add(value.getDatabase());
            builder.add(value.getRequestType().getType());
            builder.add(value.getRequest());
            builder.add(ValueType.OBJECT);
            for (final Entry<String, String> entry : value.getQueryParam().entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
            builder.close();
            builder.add(ValueType.OBJECT);
            for (final Entry<String, String> entry : value.getHeaderParam().entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
            builder.close();
            builder.close();
        }
    };

    public static final VPackSerializer<AuthenticationRequest> AUTH_REQUEST = new VPackSerializer<AuthenticationRequest>() {
        @Override
        public void serialize(
                final VPackBuilder builder,
                final String attribute,
                final AuthenticationRequest value,
                final VPackSerializationContext context) throws VPackException {
            builder.add(attribute, ValueType.ARRAY);
            builder.add(value.getVersion());
            builder.add(value.getType());
            builder.add(value.getEncryption());
            builder.add(value.getUser());
            builder.add(value.getPassword());
            builder.close();
        }
    };

    public static final VPackSerializer<CollectionType> COLLECTION_TYPE = new VPackSerializer<CollectionType>() {
        @Override
        public void serialize(
                final VPackBuilder builder,
                final String attribute,
                final CollectionType value,
                final VPackSerializationContext context) throws VPackException {
            builder.add(attribute, value.getType());
        }
    };

    public static final VPackSerializer<C8DynamoType> C8_DYNAMO_TYPE = new VPackSerializer<C8DynamoType>() {
        @Override
        public void serialize(
            final VPackBuilder builder,
            final String attribute,
            final C8DynamoType value,
            final VPackSerializationContext context) throws VPackException {
            builder.add(attribute, value.getKey());
        }
    };

    public static final VPackSerializer<C8DynamoAttributeType> C8_DYNAMO_ATTRIBUTE_TYPE = new VPackSerializer<C8DynamoAttributeType>() {
        @Override
        public void serialize(
            final VPackBuilder builder,
            final String attribute,
            final C8DynamoAttributeType value,
            final VPackSerializationContext context) throws VPackException {
            builder.add(attribute, value.getKey());
        }
    };

    public static final VPackSerializer<BaseDocument> BASE_DOCUMENT = new VPackSerializer<BaseDocument>() {
        @Override
        public void serialize(
                final VPackBuilder builder,
                final String attribute,
                final BaseDocument value,
                final VPackSerializationContext context) throws VPackException {
            final Map<String, Object> doc = new HashMap<String, Object>();
            doc.putAll(value.getProperties());
            doc.put(DocumentField.Type.ID.getSerializeName(), value.getId());
            doc.put(DocumentField.Type.KEY.getSerializeName(), value.getKey());
            doc.put(DocumentField.Type.REV.getSerializeName(), value.getRevision());
            context.serialize(builder, attribute, doc);
        }
    };

    public static final VPackSerializer<BaseEdgeDocument> BASE_EDGE_DOCUMENT = new VPackSerializer<BaseEdgeDocument>() {
        @Override
        public void serialize(
                final VPackBuilder builder,
                final String attribute,
                final BaseEdgeDocument value,
                final VPackSerializationContext context) throws VPackException {
            final Map<String, Object> doc = new HashMap<String, Object>();
            doc.putAll(value.getProperties());
            doc.put(DocumentField.Type.ID.getSerializeName(), value.getId());
            doc.put(DocumentField.Type.KEY.getSerializeName(), value.getKey());
            doc.put(DocumentField.Type.REV.getSerializeName(), value.getRevision());
            doc.put(DocumentField.Type.FROM.getSerializeName(), value.getFrom());
            doc.put(DocumentField.Type.TO.getSerializeName(), value.getTo());
            context.serialize(builder, attribute, doc);
        }
    };

    public static final VPackSerializer<TraversalOptions.Order> TRAVERSAL_ORDER = new VPackSerializer<TraversalOptions.Order>() {
        @Override
        public void serialize(
                final VPackBuilder builder,
                final String attribute,
                final Order value,
                final VPackSerializationContext context) throws VPackException {
            if (TraversalOptions.Order.preorder_expander == value) {
                builder.add(attribute, "preorder-expander");
            } else {
                builder.add(attribute, value.name());
            }
        }
    };

    public static final VPackSerializer<LogLevel> LOG_LEVEL = new VPackSerializer<LogLevel>() {
        @Override
        public void serialize(
                final VPackBuilder builder,
                final String attribute,
                final LogLevel value,
                final VPackSerializationContext context) throws VPackException {
            builder.add(attribute, value.getLevel());
        }
    };

    public static final VPackSerializer<Permissions> PERMISSIONS = new VPackSerializer<Permissions>() {
        @Override
        public void serialize(
                final VPackBuilder builder,
                final String attribute,
                final Permissions value,
                final VPackSerializationContext context) throws VPackException {
            builder.add(attribute, value.toString().toLowerCase());
        }
    };

    public static final VPackSerializer<ReplicationFactor> REPLICATION_FACTOR = new VPackSerializer<ReplicationFactor>() {
        @Override
        public void serialize(
                final VPackBuilder builder,
                final String attribute,
                final ReplicationFactor value,
                final VPackSerializationContext context) throws VPackException {
            final Boolean satellite = value.getSatellite();
            if (Boolean.TRUE == satellite) {
                builder.add(attribute, "satellite");
            } else if (value.getReplicationFactor() != null) {
                builder.add(attribute, value.getReplicationFactor());
            }
        }
    };

    public static final VPackSerializer<MinReplicationFactor> MIN_REPLICATION_FACTOR = new VPackSerializer<MinReplicationFactor>() {
        @Override
        public void serialize(
                final VPackBuilder builder,
                final String attribute,
                final MinReplicationFactor value,
                final VPackSerializationContext context) throws VPackException {
            if (value.getMinReplicationFactor() != null) {
                builder.add(attribute, value.getMinReplicationFactor());
            }
        }
    };
}
