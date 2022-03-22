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

package com.c8db.internal.net;

import com.c8db.C8DBException;
import com.c8db.Service;

import java.io.IOException;
import java.util.List;

/**
 *
 */
public class FallbackHostHandler implements HostHandler {

    private Host current;
    private Host lastSuccess;
    private int iterations;
    private final HostResolver resolver;
    private Service service;
    private boolean firstOpened;
    private boolean initialized;

    public FallbackHostHandler(final HostResolver resolver) {
        this.resolver = resolver;
        iterations = 0;
        firstOpened = true;
    }

    @Override
    public void service(Service name) {
        service = name;
        if (!initialized) {
            current = lastSuccess = resolver.resolve(name,true, false).getHostsList().get(0);
            initialized = true;
        }
    }

    @Override
    public Host get(final HostHandle hostHandle, AccessType accessType) {
        if (current != lastSuccess || iterations < 3) {
            return current;
        } else {
            reset();
            throw new C8DBException("Cannot contact any host!");
        }
    }

    @Override
    public void success() {
        lastSuccess = current;
    }

    @Override
    public void fail() {
        final List<Host> hosts = resolver.resolve(service, false, false).getHostsList();
        final int index = hosts.indexOf(current) + 1;
        final boolean inBound = index < hosts.size();
        current = hosts.get(inBound ? index : 0);
        if (!inBound) {
            iterations++;
        }
    }

    @Override
    public void reset() {
        iterations = 0;
    }

    @Override
    public void confirm() {
        if (firstOpened) {
            // after first successful established connection, update host list
            resolver.resolve(service, false, false);
            firstOpened = false;
        }
    }

    @Override
    public void close() throws IOException {
        final HostSet hosts = resolver.resolve(service, false, false);
        hosts.close();
    }

    @Override
    public void closeCurrentOnError() {
        current.closeOnError();
    }

}
