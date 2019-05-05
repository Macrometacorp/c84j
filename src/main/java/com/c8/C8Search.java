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

import com.c8.entity.ViewEntity;
import com.c8.entity.c8search.C8SearchPropertiesEntity;
import com.c8.model.c8search.C8SearchCreateOptions;
import com.c8.model.c8search.C8SearchPropertiesOptions;

/**
 * Interface for operations on C8DB view level for C8Search views.
 * 
 * @see <a href="https://docs.c8db.com/current/HTTP/Views/">View API Documentation</a>
 * @author Mark Vollmary
 * @since C8DB 3.4.0
 */
public interface C8Search extends C8View {

	/**
	 * Creates a view, then returns view information from the server.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Views/C8Search.html#create-c8search-view">API
	 *      Documentation</a>
	 * @return information about the view
	 * @throws C8DBException
	 */
	ViewEntity create() throws C8DBException;

	/**
	 * Creates a view with the given {@code options}, then returns view information from the server.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Views/C8Search.html#create-c8search-view">API
	 *      Documentation</a>
	 * @param options
	 *            Additional options, can be null
	 * @return information about the view
	 * @throws C8DBException
	 */
	ViewEntity create(C8SearchCreateOptions options) throws C8DBException;

	/**
	 * Reads the properties of the specified view.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Views/Getting.html#read-properties-of-a-view">API
	 *      Documentation</a>
	 * @return properties of the view
	 * @throws C8DBException
	 */
	C8SearchPropertiesEntity getProperties() throws C8DBException;

	/**
	 * Partially changes properties of the view.
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/Views/C8Search.html#partially-changes-properties-of-an-c8search-view">API
	 *      Documentation</a>
	 * @param options
	 *            properties to change
	 * @return properties of the view
	 * @throws C8DBException
	 */
	C8SearchPropertiesEntity updateProperties(C8SearchPropertiesOptions options) throws C8DBException;

	/**
	 * Changes properties of the view.
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/Views/C8Search.html#change-properties-of-an-c8search-view">API
	 *      Documentation</a>
	 * @param options
	 *            properties to change
	 * @return properties of the view
	 * @throws C8DBException
	 */
	C8SearchPropertiesEntity replaceProperties(C8SearchPropertiesOptions options) throws C8DBException;

}
