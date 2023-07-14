/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import com.c8db.C8Secret;
import com.c8db.Service;
import com.c8db.entity.C8SecretEntity;
import com.c8db.model.C8SecretOptions;

public class C8SecretImpl extends InternalC8Secret<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements C8Secret {

    protected C8SecretImpl(final C8DatabaseImpl db) {
        super(db);
    }

    @Override
    public C8SecretEntity create(final C8SecretOptions secretOptions) {
        return executor.execute(createSecretRequest(secretOptions),
                secretEntityResponseDeserializer(), null, Service.C8KMS);
    }

    @Override
    public C8SecretEntity get(final String name) {
        return executor.execute(getSecretRequest(name),
                secretEntityResponseDeserializer(), null, Service.C8KMS);
    }

    @Override
    public C8SecretEntity update(final C8SecretOptions secretOptions) {
        return executor.execute(updateSecretRequest(secretOptions),
                secretEntityResponseDeserializer(), null, Service.C8KMS);
    }

    @Override
    public void delete(final String name) {
        executor.execute(deleteSecretRequest(name), Void.class, null, Service.C8KMS);
    }

}
