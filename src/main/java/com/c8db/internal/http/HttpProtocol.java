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

package com.c8db.internal.http;

import java.io.IOException;

import com.c8db.Service;
import org.apache.http.client.ClientProtocolException;

import com.c8db.C8DBException;
import com.c8db.internal.net.CommunicationProtocol;
import com.c8db.internal.net.HostHandle;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.Response;

/**
 *
 */
public class HttpProtocol implements CommunicationProtocol {

    private final HttpCommunication httpCommunitaction;

    public HttpProtocol(final HttpCommunication httpCommunitaction) {
        super();
        this.httpCommunitaction = httpCommunitaction;
    }

    @Override
    public Response execute(final Request request, final HostHandle hostHandle, Service service) throws C8DBException {
        try {
            return httpCommunitaction.execute(request, hostHandle, service);
        } catch (final ClientProtocolException e) {
            throw new C8DBException(e);
        } catch (final IOException e) {
            throw new C8DBException(e);
        }
    }

    @Override
    public void close() throws IOException {
        httpCommunitaction.close();
    }

}
