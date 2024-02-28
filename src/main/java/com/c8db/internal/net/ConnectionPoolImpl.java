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
 *
 *  Modifications copyright (c) 2024 Macrometa Corp All rights reserved.
 */

package com.c8db.internal.net;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.c8db.C8DBException;
import com.c8db.Service;
import com.c8db.internal.velocystream.internal.VstConnection;
import com.c8db.internal.velocystream.internal.VstConnectionSync;

/**
 *
 */
public class ConnectionPoolImpl implements ConnectionPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPoolImpl.class);

    private final HostDescription host;
    private final int maxConnections;
    private final Stack<Connection> connections;
    private final Set<Connection> usedConnections;
    private final Queue<CompletableFuture<Connection>> waitingQueue;
    private final ConnectionFactory factory;
    private final Service service;

    public ConnectionPoolImpl(final HostDescription host, final Integer maxConnections,
            final ConnectionFactory factory, Service service) {
        super();
        this.host = host;
        this.maxConnections = maxConnections;
        this.factory = factory;
        this.service = service;
        this.connections = new Stack<>();
        this.usedConnections = new HashSet<>();
        this.waitingQueue = new LinkedList<>();
    }

    @Override
    public ManagedConnection<Connection> connection() {
        final Connection connection = getConnection();

        if (connection instanceof VstConnectionSync) {
            LOGGER.debug("Return Connection " + ((VstConnection) connection).getConnectionName());
        }

        return new ManagedConnection<>(connection, this);
    }

    @Override
    public void close() throws IOException {
        synchronized (this) {
            if (!usedConnections.isEmpty()) {
                throw new C8DBException("Attempting to close connection pool with active connections remaining.");
            }
        }
        for (final Connection connection : connections) {
            connection.close();
        }
        connections.clear();
    }

    @Override
    public void dispose(Connection connection) {
        synchronized (this) {
            if (!usedConnections.remove(connection)) {
                throw new C8DBException("Connection disposed to incorrect connection pool.");
            }
            // Check if there are any threads waiting to get connections
            CompletableFuture<Connection> future;
            while ((future = waitingQueue.poll()) != null && future.isCancelled()) {};
            if (future != null) {
            	usedConnections.add(connection);
                future.complete(connection);
            } else {
                // No threads waiting for connections
                connections.push(connection);
            }
        }
    }

    @Override
    public String toString() {
        return "ConnectionPoolImpl [host=" + host + ", maxConnections=" + maxConnections + ", connections="
                + connections.size() + ", usedconnections=" + usedConnections.size() + ", factory=" + factory.getClass().getSimpleName() + "]";
    }
    
    private Connection createConnection(final HostDescription host) {
        return factory.create(host, service);
    }
    
    private Connection getConnection() {
        CompletableFuture<Connection> future;
        synchronized (this) {
            if (!connections.empty()) {
                Connection connection = connections.pop();
                usedConnections.add(connection);
                return connection;
            } else if (usedConnections.size() < maxConnections) {
                Connection connection = createConnection(host);
                usedConnections.add(connection);
                return connection;
            } else {
                future = new CompletableFuture<>();
                waitingQueue.add(future);
            }
        }
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new C8DBException(e);
        }
    }

}
