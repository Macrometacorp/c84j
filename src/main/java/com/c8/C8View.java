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

/**
 * Interface for operations on C8DB view level.
 * 
 * @see <a href="https://docs.c8db.com/current/HTTP/Views/">View API Documentation</a>
 * @author Mark Vollmary
 * @since C8DB 3.4.0
 */
public interface C8View extends C8SerializationAccessor {

	/**
	 * The the handler of the database the collection is within
	 * 
	 * @return database handler
	 */
	public C8Database db();

	/**
	 * The name of the view
	 * 
	 * @return view name
	 */
	public String name();

	/**
	 * Checks whether the view exists.
	 * 
	 * @return true if the view exists, otherwise false
	 * @throws C8DBException
	 */
	boolean exists() throws C8DBException;

	/**
	 * Deletes the view from the database.
	 * 
	 * @see <a href= "https://docs.c8db.com/current/HTTP/Views/Creating.html#drops-a-view">API Documentation</a>
	 * @throws C8DBException
	 */
	void drop() throws C8DBException;

	/**
	 * Renames the view.
	 * 
	 * @see <a href= "https://docs.c8db.com/current/HTTP/Views/Modifying.html#rename-view">API Documentation</a>
	 * @param newName
	 *            The new name
	 * @return information about the view
	 * @throws C8DBException
	 */
	ViewEntity rename(String newName) throws C8DBException;

	/**
	 * Returns information about the view.
	 * 
	 * @see <a href= "https://docs.c8db.com/current/HTTP/Views/Getting.html#return-information-about-a-view">API
	 *      Documentation</a>
	 * @return information about the view
	 * @throws C8DBException
	 */
	ViewEntity getInfo() throws C8DBException;

}
