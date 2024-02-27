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
 *  Modifications copyright (c) 2024 Macrometa Corp All rights reserved.
 */

package com.c8db.internal.net;

import java.io.IOException;

/**
 *
 */
public interface Host {

    HostDescription getDescription();

    ManagedConnection<Connection> connection();

    void closeOnError();

    void close() throws IOException;

    void setMarkforDeletion(boolean markforDeletion);

    boolean isMarkforDeletion();
}
