/*
 * DISCLAIMER
 *
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

package com.c8.internal.net;

import java.util.Collection;
import java.util.List;

import com.c8.C8DBException;

/**
 * 
 *
 */
public interface HostResolver {

	public interface EndpointResolver {
		Collection<String> resolve(boolean closeConnections) throws C8DBException;
	}

	void init(final EndpointResolver resolver);

	List<Host> resolve(boolean initial, boolean closeConnections);

}
