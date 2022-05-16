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
 * Modifications copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

import java.io.Serializable;
import java.util.Collection;

import com.arangodb.velocypack.VPackSlice;

/**
 */
public class C8qlQueryOptions implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean count;
    private String query;
    private Boolean cache;
    private Options options;
    private VPackSlice bindVars;
    private Long batchSize;
    private Long ttl;

    public C8qlQueryOptions() {
        super();
    }

    public Boolean getCount() {
        return count;
    }

    /**
     * @param count indicates whether the number of documents in the result set
     *              should be returned in the "count" attribute of the result.
     *              Calculating the "count" attribute might have a performance
     *              impact for some queries in the future so this option is turned
     *              off by default, and "count" is only returned when requested.
     * @return options
     */
    public C8qlQueryOptions count(final Boolean count) {
        this.count = count;
        return this;
    }

    public Boolean getCache() {
        return cache;
    }

    /**
     * @param cache flag to determine whether the AQL query cache shall be used. If
     *              set to false, then any query cache lookup will be skipped for
     *              the query. If set to true, it will lead to the query cache being
     *              checked for the query if the query cache mode is either on or
     *              demand.
     * @return options
     */
    public C8qlQueryOptions cache(final Boolean cache) {
        this.cache = cache;
        return this;
    }

    protected VPackSlice getBindVars() {
        return bindVars;
    }

    /**
     * @param bindVars key/value pairs representing the bind parameters
     * @return options
     */
    protected C8qlQueryOptions bindVars(final VPackSlice bindVars) {
        this.bindVars = bindVars;
        return this;
    }

    protected String getQuery() {
        return query;
    }

    /**
     * @param query the query which you want parse
     * @return options
     */
    protected C8qlQueryOptions query(final String query) {
        this.query = query;
        return this;
    }

    /**
     * @return If set to true, then the additional query profiling information will
     *         be returned in the sub-attribute profile of the extra return
     *         attribute if the query result is not served from the query cache.
     */
    public Boolean getProfile() {
        return options != null ? options.profile : null;
    }

    /**
     * @param profile If set to true, then the additional query profiling
     *                information will be returned in the sub-attribute profile of
     *                the extra return attribute if the query result is not served
     *                from the query cache.
     * @return options
     */
    public C8qlQueryOptions profile(final Boolean profile) {
        getOptions().profile = profile;
        return this;
    }

    public Collection<String> getRules() {
        return options != null ? options.optimizer != null ? options.optimizer.rules : null : null;
    }

    /**
     * @param rules A list of to-be-included or to-be-excluded optimizer rules can
     *              be put into this attribute, telling the optimizer to include or
     *              exclude specific rules. To disable a rule, prefix its name with
     *              a -, to enable a rule, prefix it with a +. There is also a
     *              pseudo-rule all, which will match all optimizer rules
     * @return options
     */
    public C8qlQueryOptions rules(final Collection<String> rules) {
        getOptions().getOptimizer().rules = rules;
        return this;
    }

    /**
     * @param batchSize the batchsize of the result to be processed by the cursor
     * @return options
     */
    public C8qlQueryOptions batchSize(final Long batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    /**
     * @param ttl the time-to-live for the cursor
     * @return options
     */
    public C8qlQueryOptions ttl(final Long ttl) {
        this.ttl = ttl;
        return this;
    }

    public Long getBatchSize() {
        return batchSize;
    }

    public Long getTtl() {
        return ttl;
    }

    private Options getOptions() {
        if (options == null) {
            options = new Options();
        }
        return options;
    }

    private static class Options implements Serializable {

        private static final long serialVersionUID = 1L;

        private Boolean profile;
        private Optimizer optimizer;

        protected Optimizer getOptimizer() {
            if (optimizer == null) {
                optimizer = new Optimizer();
            }
            return optimizer;
        }

        private static class Optimizer {
            private Collection<String> rules;
        }
    }
}
