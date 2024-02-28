/**
 * Copyright (c) 2024 Macrometa Corp All rights reserved.
 */
package com.c8db.internal.net;

import java.io.IOException;

public class ManagedConnection<C extends Connection> implements AutoCloseable {
    
    private C connection;
    private ConnectionDisposer disposer;
    
    public ManagedConnection(C connection, ConnectionDisposer disposer) {
        this.connection = connection;
        this.disposer = disposer;
    }
    
    public C connection() {
        return this.connection;
    }
    
    public <C2 extends C> ManagedConnection<C2> castConnection() {
        final C2 resource = (C2) this.connection;
        final ConnectionDisposer disposer = this.disposer;
        this.disposer = null;
        return new ManagedConnection<>(resource, disposer);
    }

    @Override
    public void close() throws IOException {
        if (disposer != null) {
            disposer.dispose(connection);
        }
    }

}
