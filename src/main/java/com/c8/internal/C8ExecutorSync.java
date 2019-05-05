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

package com.c8.internal;

import java.io.IOException;
import java.lang.reflect.Type;

import com.arangodb.velocypack.exception.VPackException;
import com.c8.C8DBException;
import com.c8.internal.net.CommunicationProtocol;
import com.c8.internal.net.HostHandle;
import com.c8.internal.util.C8SerializationFactory;
import com.c8.velocystream.Request;
import com.c8.velocystream.Response;

/**
 * 
 *
 */
public class C8ExecutorSync extends C8Executor {

	private final CommunicationProtocol protocol;

	public C8ExecutorSync(final CommunicationProtocol protocol, final C8SerializationFactory util,
		final DocumentCache documentCache) {
		super(util, documentCache);
		this.protocol = protocol;
	}

	public <T> T execute(final Request request, final Type type) throws C8DBException {
		return execute(request, type, null);
	}

	public <T> T execute(final Request request, final Type type, final HostHandle hostHandle) throws C8DBException {
		return execute(request, new ResponseDeserializer<T>() {
			@Override
			public T deserialize(final Response response) throws VPackException {
				return createResult(type, response);
			}
		}, hostHandle);
	}

	public <T> T execute(final Request request, final ResponseDeserializer<T> responseDeserializer)
			throws C8DBException {
		return execute(request, responseDeserializer, null);
	}

	public <T> T execute(
		final Request request,
		final ResponseDeserializer<T> responseDeserializer,
		final HostHandle hostHandle) throws C8DBException {
		try {
			final Response response = protocol.execute(request, hostHandle);
			return responseDeserializer.deserialize(response);
		} catch (final VPackException e) {
			throw new C8DBException(e);
		}
	}

	public void disconnect() {
		try {
			protocol.close();
		} catch (final IOException e) {
			throw new C8DBException(e);
		}
	}
}
