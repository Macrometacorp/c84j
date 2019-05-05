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
 *
 */

package com.c8;

import com.arangodb.velocypack.VPackSlice;
import com.c8.velocystream.Response;

/**
 * Interface for a specific path to be used to perform arbitrary requests.
 * 
 * @author Mark Vollmary
 */
public interface C8Route extends C8SerializationAccessor {

	/**
	 * Returns a new {@link C8Route} instance for the given path (relative to the current route) that can be used to
	 * perform arbitrary requests.
	 * 
	 * @param path
	 *            The relative URL of the route
	 * @return {@link C8Route}
	 */
	C8Route route(String... path);

	/**
	 * Header that should be sent with each request to the route.
	 * 
	 * @param key
	 *            Header key
	 * @param value
	 *            Header value (the {@code toString()} method will be called for the value}
	 * @return {@link C8Route}
	 */
	C8Route withHeader(String key, Object value);

	/**
	 * Query parameter that should be sent with each request to the route.
	 * 
	 * @param key
	 *            Query parameter key
	 * @param value
	 *            Query parameter value (the {@code toString()} method will be called for the value}
	 * @return {@link C8Route}
	 */
	C8Route withQueryParam(String key, Object value);

	/**
	 * The response body. The body will be serialized to {@link VPackSlice}.
	 * 
	 * @param body
	 *            The request body
	 * @return {@link C8Route}
	 */
	C8Route withBody(Object body);

	/**
	 * Performs a DELETE request to the given URL and returns the server response.
	 * 
	 * @return server response
	 * @throws C8DBException
	 */
	Response delete() throws C8DBException;

	/**
	 * Performs a GET request to the given URL and returns the server response.
	 * 
	 * @return server response
	 * @throws C8DBException
	 */

	Response get() throws C8DBException;

	/**
	 * Performs a HEAD request to the given URL and returns the server response.
	 * 
	 * @return server response
	 * @throws C8DBException
	 */

	Response head() throws C8DBException;

	/**
	 * Performs a PATCH request to the given URL and returns the server response.
	 * 
	 * @return server response
	 * @throws C8DBException
	 */

	Response patch() throws C8DBException;

	/**
	 * Performs a POST request to the given URL and returns the server response.
	 * 
	 * @return server response
	 * @throws C8DBException
	 */

	Response post() throws C8DBException;

	/**
	 * Performs a PUT request to the given URL and returns the server response.
	 * 
	 * @return server response
	 * @throws C8DBException
	 */

	Response put() throws C8DBException;

}
