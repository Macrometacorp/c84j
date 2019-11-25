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

public class C8StreamCreateOptions {

    private Boolean isLocal;

    public C8StreamCreateOptions() {
        super();
    }

    public Boolean getIsLocal() {
        return isLocal;
    }
    
    /**
     * @param isLocal Operate on a local stream instead of a global one. Default value: false
     * @return options
     */
    public C8StreamCreateOptions isLocal(final Boolean isLocal) {
        this.isLocal = isLocal;
        return this;
    }

}
