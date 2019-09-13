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

package com.c8db.model;

/**
 *
 */
public class DBCreateOptions {

    private String name;

    private Options options;

    public DBCreateOptions() {
        super();
    }

    public String getName() {
        return name;
    }

    public Options getOptions() {
        return options;
    }
    /**
     * @param name Has to contain a valid database name
     * @return options
     */
    protected DBCreateOptions name(final String name) {
        this.name = name;
        return this;
    }

    /**
     * 
     * @param options Has to contain a valid dc
     * @return options
     */
    protected DBCreateOptions options(final String spotDc, final String dcList) {
        Options options = new Options();
        options.setSpotDc(spotDc);
        options.setDcList(dcList);
        this.options = options;
        return this;
    }
    public class Options {
        private String dcList;
        private String spotDc;
        
        public String getDcList() {
            return dcList;
        }

        public void setDcList(String dcList) {
            this.dcList = dcList;
        }

        public String getSpotDc() {
            return spotDc;
        }

        public void setSpotDc(String spotDc) {
            this.spotDc = spotDc;
        }
        
    }
}
