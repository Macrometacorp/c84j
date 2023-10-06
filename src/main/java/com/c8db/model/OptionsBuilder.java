/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */

package com.c8db.model;

import com.arangodb.velocypack.VPackSlice;
import com.c8db.C8KeyValue;
import com.c8db.entity.EdgeDefinition;
import com.c8db.entity.Permissions;
import com.c8db.entity.UserQueryOptions;

import java.util.Collection;
import java.util.HashMap;

/**
 *
 */
public class OptionsBuilder {

    private OptionsBuilder() {
        super();
    }

    public static UserCreateOptions build(final UserCreateOptions options, final String user, final String passwd, final String email) {
        return options.user(user).passwd(passwd).email(email);
    }

    public static HashIndexOptions build(final HashIndexOptions options, final Iterable<String> fields) {
        return options.fields(fields);
    }

    public static SkiplistIndexOptions build(final SkiplistIndexOptions options, final Iterable<String> fields) {
        return options.fields(fields);
    }

    public static PersistentIndexOptions build(final PersistentIndexOptions options, final Iterable<String> fields) {
        return options.fields(fields);
    }

    public static GeoIndexOptions build(final GeoIndexOptions options, final Iterable<String> fields) {
        return options.fields(fields);
    }

    public static FulltextIndexOptions build(final FulltextIndexOptions options, final Iterable<String> fields) {
        return options.fields(fields);
    }

    public static TTLIndexOptions build(final TTLIndexOptions options, final Iterable<String> fields) {
        return options.fields(fields);
    }

    public static CollectionCreateOptions build(final CollectionCreateOptions options, final String name) {
        return options.name(name);
    }

    public static C8KVCreateBodyOptions build(final C8KVCreateOptions options) {
        return new C8KVCreateBodyOptions()
                .stream(options.hasStream())
                .enableShards(options.isEnableShards())
                .waitForSync(options.isWaitForSync())
                .blobs(options.isBlobs())
                .shardKeys(options.getShardKeys())
                .strongConsistency(options.hasStrongConsistency());
    }

    public static C8qlQueryOptions build(final C8qlQueryOptions options, final String query, final VPackSlice bindVars) {
        return options.query(query).bindVars(bindVars);
    }

    public static C8qlQueryExplainOptions build(final C8qlQueryExplainOptions options, final String query,
                                                final VPackSlice bindVars) {
        return options.query(query).bindVars(bindVars);
    }

    public static C8qlQueryParseOptions build(final C8qlQueryParseOptions options, final String query) {
        return options.query(query);
    }

    public static GraphCreateOptions build(final GraphCreateOptions options, final String name,
                                           final Collection<EdgeDefinition> edgeDefinitions) {
        return options.name(name).edgeDefinitions(edgeDefinitions);
    }

    public static C8TransactionOptions build(final C8TransactionOptions options, final String action) {
        return options.action(action);
    }

    public static CollectionRenameOptions build(final CollectionRenameOptions options, final String name) {
        return options.name(name);
    }

    public static DBCreateOptions build(final DBCreateOptions options, final String tenant, final String name, final String spotDc,
                                        final String dcList) {
        return options.geoFabric(tenant, name).options(spotDc, dcList);
    }

    public static DCListOptions build(final DCListOptions options, final String dcList) {
        return options.dcList(dcList);
    }

    public static UserAccessOptions build(final UserAccessOptions options, final Permissions grant) {
        return options.grant(grant);
    }

    public static VertexCollectionCreateOptions build(final VertexCollectionCreateOptions options,
                                                      final String collection) {
        return options.collection(collection);
    }

    public static UserQueryOptions build(final UserQueryOptions options, final String name) {
        if (options.getParameter() == null)
            options.parameter(new HashMap<String, Object>());
        return options.name(name);
    }

    public static ApiKeyOptions build(final ApiKeyOptions options, final String apiKey) {
        return options.apiKey(apiKey);
    }

    public static JwtOptions build(final JwtOptions options, final String jwt) {
        return options.jwt(jwt);
    }

    public static ApiKeyCreateOptions build(final ApiKeyCreateOptions options, final String keyId,
                                            String onBehalfOfUser, boolean isSystem) {
        options.setKeyid(keyId);
        if (onBehalfOfUser != null) {
            options.setUser(onBehalfOfUser);
            options.setIsSystem(isSystem);
        }
        return options;
    }

}
