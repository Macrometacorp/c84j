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

import java.lang.reflect.Field;
import java.util.Date;

import com.arangodb.velocypack.VPackFieldNamingStrategy;
import com.arangodb.velocypack.VPackModule;
import com.arangodb.velocypack.VPackParserModule;
import com.arangodb.velocypack.VPackParserSetupContext;
import com.arangodb.velocypack.VPackSetupContext;
import com.c8db.entity.BaseDocument;
import com.c8db.entity.BaseEdgeDocument;
import com.c8db.entity.CollectionStatus;
import com.c8db.entity.CollectionType;
import com.c8db.entity.DocumentField;
import com.c8db.entity.FxType;
import com.c8db.entity.License;
import com.c8db.entity.LogLevel;
import com.c8db.entity.MinReplicationFactor;
import com.c8db.entity.Permissions;
import com.c8db.entity.QueryEntity;
import com.c8db.entity.QueryExecutionState;
import com.c8db.entity.ReplicationFactor;
import com.c8db.internal.velocystream.internal.AuthenticationRequest;
import com.c8db.model.TraversalOptions;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.Response;

/**
 *
 */
public class VPackDriverModule implements VPackModule, VPackParserModule {

    @Override
    public <C extends VPackSetupContext<C>> void setup(final C context) {
        context.fieldNamingStrategy(new VPackFieldNamingStrategy() {
            @Override
            public String translateName(final Field field) {
                final DocumentField annotation = field.getAnnotation(DocumentField.class);
                if (annotation != null) {
                    return annotation.value().getSerializeName();
                }
                return field.getName();
            }
        });
        context.registerSerializer(Request.class, VPackSerializers.REQUEST);
        context.registerSerializer(AuthenticationRequest.class, VPackSerializers.AUTH_REQUEST);
        context.registerSerializer(CollectionType.class, VPackSerializers.COLLECTION_TYPE);
        context.registerSerializer(BaseDocument.class, VPackSerializers.BASE_DOCUMENT);
        context.registerSerializer(BaseEdgeDocument.class, VPackSerializers.BASE_EDGE_DOCUMENT);
        context.registerSerializer(TraversalOptions.Order.class, VPackSerializers.TRAVERSAL_ORDER);
        context.registerSerializer(LogLevel.class, VPackSerializers.LOG_LEVEL);
        context.registerSerializer(Permissions.class, VPackSerializers.PERMISSIONS);
        context.registerSerializer(ReplicationFactor.class, VPackSerializers.REPLICATION_FACTOR);
        context.registerSerializer(MinReplicationFactor.class, VPackSerializers.MIN_REPLICATION_FACTOR);

        context.registerDeserializer(Response.class, VPackDeserializers.RESPONSE);
        context.registerDeserializer(CollectionType.class, VPackDeserializers.COLLECTION_TYPE);
        context.registerDeserializer(CollectionStatus.class, VPackDeserializers.COLLECTION_STATUS);
        context.registerDeserializer(BaseDocument.class, VPackDeserializers.BASE_DOCUMENT);
        context.registerDeserializer(BaseEdgeDocument.class, VPackDeserializers.BASE_EDGE_DOCUMENT);
        context.registerDeserializer(QueryEntity.PROPERTY_STARTED, Date.class, VPackDeserializers.DATE_STRING);
        context.registerDeserializer(LogLevel.class, VPackDeserializers.LOG_LEVEL);
        context.registerDeserializer(License.class, VPackDeserializers.LICENSE);
        context.registerDeserializer(Permissions.class, VPackDeserializers.PERMISSIONS);
        context.registerDeserializer(QueryExecutionState.class, VPackDeserializers.QUERY_EXECUTION_STATE);
        context.registerDeserializer(FxType.class, VPackDeserializers.FX_TYPE);
        context.registerDeserializer(ReplicationFactor.class, VPackDeserializers.REPLICATION_FACTOR);
        context.registerDeserializer(MinReplicationFactor.class, VPackDeserializers.MIN_REPLICATION_FACTOR);
    }

    @Override
    public <C extends VPackParserSetupContext<C>> void setup(final C context) {

    }

}
