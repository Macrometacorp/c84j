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

package com.c8.internal.velocypack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.velocypack.VPackDeserializationContext;
import com.arangodb.velocypack.VPackDeserializer;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8.entity.C8DBVersion;
import com.c8.entity.BaseDocument;
import com.c8.entity.BaseEdgeDocument;
import com.c8.entity.CollectionStatus;
import com.c8.entity.CollectionType;
import com.c8.entity.LogLevel;
import com.c8.entity.Permissions;
import com.c8.entity.QueryExecutionState;
import com.c8.entity.ReplicationFactor;
import com.c8.entity.ViewEntity;
import com.c8.entity.ViewType;
import com.c8.entity.C8DBVersion.License;
import com.c8.entity.c8search.C8SearchProperties;
import com.c8.entity.c8search.C8SearchPropertiesEntity;
import com.c8.entity.c8search.CollectionLink;
import com.c8.entity.c8search.ConsolidationPolicy;
import com.c8.entity.c8search.ConsolidationType;
import com.c8.entity.c8search.FieldLink;
import com.c8.entity.c8search.StoreValuesType;
import com.c8.velocystream.Response;

/**
 * 
 *
 */
public class VPackDeserializers {

	private static final Logger LOGGER = LoggerFactory.getLogger(VPackDeserializers.class);
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	public static final VPackDeserializer<Response> RESPONSE = new VPackDeserializer<Response>() {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Response deserialize(
			final VPackSlice parent,
			final VPackSlice vpack,
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
		public CollectionType deserialize(
			final VPackSlice parent,
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			return CollectionType.fromType(vpack.getAsInt());
		}
	};

	public static final VPackDeserializer<CollectionStatus> COLLECTION_STATUS = new VPackDeserializer<CollectionStatus>() {
		@Override
		public CollectionStatus deserialize(
			final VPackSlice parent,
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			return CollectionStatus.fromStatus(vpack.getAsInt());
		}
	};

	@SuppressWarnings("unchecked")
	public static final VPackDeserializer<BaseDocument> BASE_DOCUMENT = new VPackDeserializer<BaseDocument>() {
		@SuppressWarnings("rawtypes")
		@Override
		public BaseDocument deserialize(
			final VPackSlice parent,
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			return new BaseDocument((Map) context.deserialize(vpack, Map.class));
		}
	};

	@SuppressWarnings("unchecked")
	public static final VPackDeserializer<BaseEdgeDocument> BASE_EDGE_DOCUMENT = new VPackDeserializer<BaseEdgeDocument>() {
		@SuppressWarnings("rawtypes")
		@Override
		public BaseEdgeDocument deserialize(
			final VPackSlice parent,
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			return new BaseEdgeDocument((Map) context.deserialize(vpack, Map.class));
		}
	};

	public static final VPackDeserializer<Date> DATE_STRING = new VPackDeserializer<Date>() {
		@Override
		public Date deserialize(
			final VPackSlice parent,
			final VPackSlice vpack,
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
		public LogLevel deserialize(
			final VPackSlice parent,
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			return LogLevel.fromLevel(vpack.getAsInt());
		}
	};

	public static final VPackDeserializer<C8DBVersion.License> LICENSE = new VPackDeserializer<C8DBVersion.License>() {
		@Override
		public License deserialize(
			final VPackSlice parent,
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			return License.valueOf(vpack.getAsString().toUpperCase());
		}
	};

	public static final VPackDeserializer<Permissions> PERMISSIONS = new VPackDeserializer<Permissions>() {
		@Override
		public Permissions deserialize(
			final VPackSlice parent,
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			return Permissions.valueOf(vpack.getAsString().toUpperCase());
		}
	};

	public static final VPackDeserializer<QueryExecutionState> QUERY_EXECUTION_STATE = new VPackDeserializer<QueryExecutionState>() {
		@Override
		public QueryExecutionState deserialize(
			final VPackSlice parent,
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			return QueryExecutionState.valueOf(vpack.getAsString().toUpperCase().replaceAll(" ", "_"));
		}
	};

	public static final VPackDeserializer<ReplicationFactor> REPLICATION_FACTOR = new VPackDeserializer<ReplicationFactor>() {
		@Override
		public ReplicationFactor deserialize(
			final VPackSlice parent,
			final VPackSlice vpack,
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

	public static final VPackDeserializer<ViewType> VIEW_TYPE = new VPackDeserializer<ViewType>() {
		@Override
		public ViewType deserialize(
			final VPackSlice parent,
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			return "c8search".equals(vpack.getAsString()) ? ViewType.c8_SEARCH
					: ViewType.valueOf(vpack.getAsString().toUpperCase());
		}
	};

	public static final VPackDeserializer<C8SearchProperties> c8_SEARCH_PROPERTIES = new VPackDeserializer<C8SearchProperties>() {
		@Override
		public C8SearchProperties deserialize(
			final VPackSlice parent,
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			final C8SearchProperties properties = new C8SearchProperties();
			final VPackSlice consolidationIntervalMsec = vpack.get("consolidationIntervalMsec");
			if (consolidationIntervalMsec.isInteger()) {
				properties.setConsolidationIntervalMsec(consolidationIntervalMsec.getAsLong());
			}
			final VPackSlice cleanupIntervalStep = vpack.get("cleanupIntervalStep");
			if (cleanupIntervalStep.isInteger()) {
				properties.setCleanupIntervalStep(cleanupIntervalStep.getAsLong());
			}
			final VPackSlice consolidationPolicy = vpack.get("consolidationPolicy");
			if (consolidationPolicy.isObject()) {
				properties.setConsolidationPolicy(
					(ConsolidationPolicy) context.deserialize(consolidationPolicy, ConsolidationPolicy.class));
			}

			final VPackSlice links = vpack.get("links");
			if (links.isObject()) {
				final Iterator<Entry<String, VPackSlice>> collectionIterator = links.objectIterator();
				for (; collectionIterator.hasNext();) {
					final Entry<String, VPackSlice> entry = collectionIterator.next();
					final VPackSlice value = entry.getValue();
					final CollectionLink link = CollectionLink.on(entry.getKey());
					final VPackSlice analyzers = value.get("analyzers");
					if (analyzers.isArray()) {
						final Iterator<VPackSlice> analyzerIterator = analyzers.arrayIterator();
						for (; analyzerIterator.hasNext();) {
							link.analyzers(analyzerIterator.next().getAsString());
						}
					}
					final VPackSlice includeAllFields = value.get("includeAllFields");
					if (includeAllFields.isBoolean()) {
						link.includeAllFields(includeAllFields.getAsBoolean());
					}
					final VPackSlice trackListPositions = value.get("trackListPositions");
					if (trackListPositions.isBoolean()) {
						link.trackListPositions(trackListPositions.getAsBoolean());
					}
					final VPackSlice storeValues = value.get("storeValues");
					if (storeValues.isString()) {
						link.storeValues(StoreValuesType.valueOf(storeValues.getAsString().toUpperCase()));
					}
					final VPackSlice fields = value.get("fields");
					if (fields.isObject()) {
						final Iterator<Entry<String, VPackSlice>> fieldsIterator = fields.objectIterator();
						for (; fieldsIterator.hasNext();) {
							link.fields(deserializeField(fieldsIterator.next()));
						}
					}
					properties.addLink(link);
				}
			}
			return properties;
		}
	};

	protected static FieldLink deserializeField(final Entry<String, VPackSlice> field) {
		final VPackSlice value = field.getValue();
		final FieldLink link = FieldLink.on(field.getKey());
		final VPackSlice analyzers = value.get("analyzers");
		if (analyzers.isArray()) {
			final Iterator<VPackSlice> analyzerIterator = analyzers.arrayIterator();
			for (; analyzerIterator.hasNext();) {
				link.analyzers(analyzerIterator.next().getAsString());
			}
		}
		final VPackSlice includeAllFields = value.get("includeAllFields");
		if (includeAllFields.isBoolean()) {
			link.includeAllFields(includeAllFields.getAsBoolean());
		}
		final VPackSlice trackListPositions = value.get("trackListPositions");
		if (trackListPositions.isBoolean()) {
			link.trackListPositions(trackListPositions.getAsBoolean());
		}
		final VPackSlice storeValues = value.get("storeValues");
		if (storeValues.isString()) {
			link.storeValues(StoreValuesType.valueOf(storeValues.getAsString().toUpperCase()));
		}
		final VPackSlice fields = value.get("fields");
		if (fields.isObject()) {
			final Iterator<Entry<String, VPackSlice>> fieldsIterator = fields.objectIterator();
			for (; fieldsIterator.hasNext();) {
				link.fields(deserializeField(fieldsIterator.next()));
			}
		}
		return link;
	}

	public static final VPackDeserializer<C8SearchPropertiesEntity> c8_SEARCH_PROPERTIES_ENTITY = new VPackDeserializer<C8SearchPropertiesEntity>() {
		@Override
		public C8SearchPropertiesEntity deserialize(
			final VPackSlice parent,
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			final ViewEntity entity = context.deserialize(vpack, ViewEntity.class);
			final C8SearchProperties properties = context.deserialize(vpack, C8SearchProperties.class);
			final C8SearchPropertiesEntity result = new C8SearchPropertiesEntity(entity.getId(),
					entity.getName(), entity.getType(), properties);
			return result;
		}
	};

	public static final VPackDeserializer<ConsolidationPolicy> CONSOLIDATE = new VPackDeserializer<ConsolidationPolicy>() {
		@Override
		public ConsolidationPolicy deserialize(
			final VPackSlice parent,
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			final VPackSlice type = vpack.get("type");
			if (type.isString()) {
				final ConsolidationPolicy consolidate = ConsolidationPolicy
						.of(ConsolidationType.valueOf(type.getAsString().toUpperCase()));
				final VPackSlice threshold = vpack.get("threshold");
				if (threshold.isNumber()) {
					consolidate.threshold(threshold.getAsDouble());
				}
				final VPackSlice segmentThreshold = vpack.get("segmentThreshold");
				if (segmentThreshold.isInteger()) {
					consolidate.segmentThreshold(segmentThreshold.getAsLong());
				}
				return consolidate;
			}
			return null;
		}
	};

}
