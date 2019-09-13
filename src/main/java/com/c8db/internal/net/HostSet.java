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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostSet {
    private static final Logger LOGGER = LoggerFactory.getLogger(HostSet.class);

    private ArrayList<Host> hosts = new ArrayList<Host>();

    public HostSet() {
        super();
    }

    public HostSet(List<Host> hosts) {
        super();

        for (Host host : hosts) {
            addHost(host);
        }

    }

    public List<Host> getHostsList() {
        return Collections.unmodifiableList(hosts);
    }

    public void addHost(Host newHost) {

        if (hosts.contains(newHost)) {
            LOGGER.debug("Host" + newHost + " allready in Set");

            for (Host host : hosts) {
                if (host.equals(newHost)) {
                    host.setMarkforDeletion(false);
                }
            }

        } else {
            hosts.add(newHost);
            LOGGER.debug("Added Host " + newHost + " - now " + hosts.size() + " Hosts in List");
        }

    }

    public void close() {
        LOGGER.debug("Close all Hosts in Set");

        for (Host host : hosts) {
            try {

                LOGGER.debug("Try to close Host " + host);
                host.close();

            } catch (IOException e) {
                LOGGER.warn("Error during closing the Host " + host, e);
            }
        }
    }

    public void markAllForDeletion() {

        for (Host host : hosts) {
            host.setMarkforDeletion(true);
        }

    }

    public void clearAllMarkedForDeletion() throws IOException {

        LOGGER.debug("Clear all Hosts in Set with markForDeletion");

        for (Host host : hosts) {
            if (host.isMarkforDeletion()) {
                try {

                    LOGGER.debug("Try to close Host " + host);
                    host.close();

                } catch (IOException e) {
                    LOGGER.warn("Error during closing the Host " + host, e);
                }
            }
        }

    }

    public void clear() {
        LOGGER.debug("Clear all Hosts in Set");

        close();
        hosts.clear();
    }
}
