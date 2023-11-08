/**
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db;

import com.c8db.internal.C8Context;
import com.c8db.internal.http.HttpCommunication;
import com.c8db.internal.net.HostHandle;
import com.c8db.internal.net.HostHandler;
import com.c8db.internal.net.HostResolver;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.internal.velocystream.VstCommunicationSync;
import com.c8db.util.C8Serialization;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.Response;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

public class TestC8DBBuilder extends C8DB.Builder {

    private Consumer<Response> consumer;
    private Consumer<Response> testConsumer;
    private int count;

    public TestC8DBBuilder() {
        consumer = response -> {
            if (testConsumer != null) {
                testConsumer.accept(response);
                count--;
                if (count == 0) {
                    testConsumer = null;
                }
            }
        };
    }

    @Override
    protected C8DB createC8DB(VstCommunicationSync.Builder vstBuilder, HttpCommunication.Builder httpBuilder,
                              C8SerializationFactory util, Protocol protocol, HostResolver hostResolver,
                              C8Context context) {
        final Map<Service, HostHandler> hostHandlerMatrix = createHostHandlerMatrix(hostResolver);
        httpBuilder = new TestHttpCommunicationBuilder(hostHandlerMatrix, consumer);
        return super.createC8DB(vstBuilder, httpBuilder, util, protocol, hostResolver, context);
    }

    public void listenResponse(Consumer<Response> testConsumer) {
        this.testConsumer = testConsumer;
        this.count = 1;
    }

    public void listenResponse(Consumer<Response> testConsumer, int count) {
        this.testConsumer = testConsumer;
        this.count = count;
    }

    public static class TestHttpCommunicationBuilder extends HttpCommunication.Builder {

        private Consumer<Response> consumer;
        private Map<Service, HostHandler> hostHandlerMatrix;
        public TestHttpCommunicationBuilder(Map<Service, HostHandler> hostHandlerMatrix, Consumer<Response> consumer) {
            super(hostHandlerMatrix);
            this.hostHandlerMatrix = hostHandlerMatrix;
            this.consumer = consumer;
        }

        public TestHttpCommunicationBuilder(HttpCommunication.Builder builder) {
            super(builder);
        }

        @Override
        public HttpCommunication build(C8Serialization util) {
            return new TestHttpCommunication(hostHandlerMatrix, consumer);
        }
    }

    public static class TestHttpCommunication extends HttpCommunication {

        private Consumer<Response> consumer;

        protected TestHttpCommunication(Map<Service, HostHandler> hostHandlerMatrix, Consumer<Response> consumer) {
            super(hostHandlerMatrix);
            this.consumer = consumer;
        }

        @Override
        public Response execute(Request request, HostHandle hostHandle, Service service) throws C8DBException, IOException {
            Response response = super.execute(request, hostHandle, service);
            consumer.accept(response);
            return response;
        }
    }
}
