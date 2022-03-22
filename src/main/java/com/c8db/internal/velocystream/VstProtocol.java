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

package com.c8db.internal.velocystream;

import java.io.IOException;

import com.c8db.C8DBException;
import com.c8db.Service;
import com.c8db.internal.net.CommunicationProtocol;
import com.c8db.internal.net.HostHandle;
import com.c8db.internal.velocystream.internal.VstConnectionSync;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.Response;

/**
 *
 */
public class VstProtocol implements CommunicationProtocol {

    private final VstCommunication<Response, VstConnectionSync> communication;

    public VstProtocol(final VstCommunication<Response, VstConnectionSync> communication) {
        super();
        this.communication = communication;
    }

    @Override
    public Response execute(final Request request, final HostHandle hostHandle, Service service) throws C8DBException {
        return communication.execute(request, hostHandle, service);
    }

    @Override
    public void close() throws IOException {
        communication.close();
    }

}
