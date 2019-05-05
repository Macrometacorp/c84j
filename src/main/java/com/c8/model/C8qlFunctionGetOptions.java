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

package com.c8.model;

/**
 * @author Mark Vollmary
 * 
 * @see <a href=
 *      "https://docs.c8db.com/current/HTTP/AqlUserFunctions/index.html#return-registered-aql-user-functions">API
 *      Documentation</a>
 */
public class C8qlFunctionGetOptions {

	private String namespace;

	public C8qlFunctionGetOptions() {
		super();
	}

	public String getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace
	 *            Returns all registered AQL user functions from namespace namespace
	 * @return options
	 */
	public C8qlFunctionGetOptions namespace(final String namespace) {
		this.namespace = namespace;
		return this;
	}

}
