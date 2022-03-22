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

package com.c8db.internal.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.c8db.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.C8DBException;
import com.c8db.internal.C8ExecutorSync;
import com.c8db.internal.C8RequestParam;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.internal.util.HostUtils;
import com.c8db.util.C8Serialization;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

/**
 *
 */
public class ExtendedHostResolver implements HostResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedHostResolver.class);

    private Map<Service, HostSet> hostMatrix;

    private final Integer maxConnections;
    private final ConnectionFactory connectionFactory;

    private long lastUpdate;
    private Integer acquireHostListInterval;

    private C8ExecutorSync executor;
    private C8Serialization arangoSerialization;

    public ExtendedHostResolver(final Map<Service, List<Host>> hostMatrix, final Integer maxConnections,
                                final ConnectionFactory connectionFactory, Integer acquireHostListInterval) {

        this.acquireHostListInterval = acquireHostListInterval;

        this.hostMatrix = toHostSetsMap(hostMatrix);
        this.maxConnections = maxConnections;
        this.connectionFactory = connectionFactory;

        lastUpdate = 0;
    }

    private Map<Service, HostSet> toHostSetsMap(Map<Service, List<Host>> hostMatrix) {
        final Map map = new HashMap();
        for (Service key: hostMatrix.keySet()) {
            map.put(key, new HostSet(hostMatrix.get(key)));
        }
        return map;
    }

    @Override
    public void init(C8ExecutorSync executor, C8Serialization arangoSerialization) {
        this.executor = executor;
        this.arangoSerialization = arangoSerialization;
    }

    @Override
    public HostSet resolve(Service service, boolean initial, boolean closeConnections) {
        if (!initial && isExpired()) {
            HostSet hosts = hostMatrix.get(Service.C8DB);
            lastUpdate = System.currentTimeMillis();

            final Collection<String> endpoints = resolveFromServer();
            LOGGER.debug("Resolve " + endpoints.size() + " Endpoints");
            LOGGER.debug("Endpoints " + Arrays.deepToString(endpoints.toArray()));

            if (!endpoints.isEmpty()) {
                hosts.markAllForDeletion();
            }

            for (final String endpoint : endpoints) {
                LOGGER.debug("Create HOST from " + endpoint);

                if (endpoint.matches(".*://.+:[0-9]+")) {

                    final String[] s = endpoint.replaceAll(".*://", "").split(":");
                    if (s.length == 2) {
                        final HostDescription description = new HostDescription(s[0], Integer.valueOf(s[1]));
                        hosts.addHost(HostUtils.createHost(description, maxConnections, connectionFactory));
                    } else if (s.length == 4) {
                        // IPV6 Address - TODO: we need a proper function to resolve AND support IPV4 &
                        // IPV6 functions
                        // globally
                        final HostDescription description = new HostDescription("127.0.0.1", Integer.valueOf(s[3]));
                        hosts.addHost(HostUtils.createHost(description, maxConnections, connectionFactory));
                    } else {
                        LOGGER.warn("Skip Endpoint (Missing Port)" + endpoint);
                    }

                } else {
                    LOGGER.warn("Skip Endpoint (Format)" + endpoint);
                }
            }

            try {
                hosts.clearAllMarkedForDeletion();
            } catch (IOException e) {
                LOGGER.error("Cant close all Hosts with MarkedForDeletion", e);
            }

        }

        HostSet hosts = hostMatrix.get(service);
        return hosts;
    }

    private Collection<String> resolveFromServer() throws C8DBException {

        Collection<String> response;

        try {

            response = executor.execute(new Request(C8RequestParam.DEMO_TENANT, C8RequestParam.SYSTEM, RequestType.GET,
                    "/_api/cluster/endpoints"), new ResponseDeserializer<Collection<String>>() {
                        @Override
                        public Collection<String> deserialize(final Response response) throws VPackException {
                            final VPackSlice field = response.getBody().get("endpoints");
                            Collection<String> endpoints;
                            if (field.isNone()) {
                                endpoints = Collections.<String>emptyList();
                            } else {
                                final Collection<Map<String, String>> tmp = arangoSerialization.deserialize(field,
                                        Collection.class);
                                endpoints = new ArrayList<String>();
                                for (final Map<String, String> map : tmp) {
                                    for (final String value : map.values()) {
                                        endpoints.add(value);
                                    }
                                }
                            }
                            return endpoints;
                        }
                    }, null);
        } catch (final C8DBException e) {
            final Integer responseCode = e.getResponseCode();
            if (responseCode != null && responseCode == 403) {
                response = Collections.<String>emptyList();
            } else {
                throw e;
            }
        }

        return response;
    }

    private boolean isExpired() {
        return System.currentTimeMillis() > (lastUpdate + acquireHostListInterval);
    }

}
