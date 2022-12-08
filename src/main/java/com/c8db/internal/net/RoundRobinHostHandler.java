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
 *
 *  Modifications copyright (c) 2022 Macrometa Corp All rights reserved.
 *
 */

package com.c8db.internal.net;

import com.c8db.Service;

import java.io.IOException;

/**
 *
 */
public class RoundRobinHostHandler implements HostHandler {

    private final HostResolver resolver;
    private final Service service;
    private int current;
    private int fails;
    private Host currentHost;

    public RoundRobinHostHandler(final HostResolver resolver, final Service service) {
        super();
        this.resolver = resolver;
        this.service = service;
        resolver.resolve(service, true, false);
        current = 0;
        fails = 0;
    }

    @Override
    public Host get(final HostHandle hostHandle, AccessType accessType) {

        final HostSet hosts = resolver.resolve(service, false, false);
        final int size = hosts.getHostsList().size();

        if (fails > size) {
            return null;
        }

        final int index = (current++) % size;
        Host host = hosts.getHostsList().get(index);
        if (hostHandle != null) {
            final HostDescription hostDescription = hostHandle.getHost();
            if (hostDescription != null) {
                for (int i = index; i < index + size; i++) {
                    host = hosts.getHostsList().get(i % size);
                    if (hostDescription.equals(host.getDescription())) {
                        break;
                    }
                }
            } else {
                hostHandle.setHost(host.getDescription());
            }
        }
        currentHost = host;
        return host;
    }

    @Override
    public void success() {
        fails = 0;
    }

    @Override
    public void fail() {
        fails++;
    }

    @Override
    public void reset() {
        fails = 0;
    }

    @Override
    public void confirm() {
    }

    @Override
    public void close() throws IOException {
        final HostSet hosts = resolver.resolve(service, false, false);
        hosts.close();
    }

    @Override
    public void closeCurrentOnError() {
        currentHost.closeOnError();
    }

}
