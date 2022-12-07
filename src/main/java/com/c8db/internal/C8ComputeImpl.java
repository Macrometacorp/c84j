/**
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.internal;

import com.c8db.C8Compute;
import com.c8db.C8DBException;
import com.c8db.Service;
import com.c8db.entity.FxEntity;
import com.c8db.entity.FxMetadataEntity;
import com.c8db.model.FxReadOptions;

import java.util.Collection;
import java.util.Map;

public class C8ComputeImpl extends InternalC8Compute<C8DBImpl, C8DatabaseImpl, C8ExecutorSync> implements C8Compute {

    public C8ComputeImpl(C8DatabaseImpl db) {
        super(db);
    }

    @Override
    public Collection<FxEntity> getFunctions() throws C8DBException {
        return getFunctions(null);
    }

    @Override
    public Collection<FxEntity> getFunctions(final FxReadOptions options) throws C8DBException {
        return executor.execute(getFunctionsRequest(options), getFunctionsResponseDeserializer(), null, Service.C8FUNCTION);
    }

    @Override
    public FxEntity getInfo(String name) throws C8DBException {
        return executor.execute(getInfoRequest(name), getInfoResponseDeserializer(), null, Service.C8FUNCTION);
    }

    @Override
    public FxMetadataEntity getMetadata() throws C8DBException {
        return executor.execute(getMetadataRequest(), getMetadataResponseDeserializer(), null, Service.C8FUNCTION);
    }

    @Override
    public Object executeFunction(String name, Map<String, Object> arguments) throws C8DBException {
        return executor.execute(executeFunctionRequest(name, arguments), executeFunctionResponseDeserializer(), null, Service.C8FUNCTION);
    }
}
