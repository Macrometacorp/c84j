package com.c8db.velocystream;

import com.arangodb.velocypack.VPackSlice;
import com.c8db.C8DBException;
import com.c8db.entity.CursorEntity;
import com.c8db.entity.Entity;
import com.c8db.entity.ErrorEntity;
import com.c8db.internal.util.IOUtils;
import com.c8db.util.C8Serialization;
import com.c8db.util.C8Serializer;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MultipartResponse extends Response {

    private List<Response> responses;

    C8Serialization util;

    public MultipartResponse(C8Serialization util){
        this.util = util;
    }

    public void parseMultipartResponse( InputStream responseStream) throws IOException {



        MultipartStream multipartStream = new MultipartStream(
                new ByteArrayInputStream(IOUtils.toByteArray(responseStream)),
                MultipartRequest.BOUNDARY.getBytes(),
                1024,    // internal buffer size (you choose)
                null);   // progress indicator (none)

        boolean nextPart = multipartStream.skipPreamble();
        List<HttpResponse> httpResponseList = new ArrayList<>();
        while (nextPart) {

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            String partHeaders = multipartStream.readHeaders();
            multipartStream.readBodyData(output);


            HttpResponse httpResponse = buildBasicHttpResponse(output.toString("UTF-8"));//buildHttpResponse(output.toString("UTF-8"));
            httpResponseList.add(httpResponse);

            Response response = buildResponse(httpResponse);
            if(response.getResponseCode() >= 400){
                ErrorEntity entity = createResult(ErrorEntity.class,response );
            }else {
                CursorEntity<T> entity = createResult(CursorEntity.class,response );
            }
            responses.add(response);

            nextPart = multipartStream.readBoundary();
        }
    }

    protected <T> T createResult(final Type type, final Response response) {
        if (type != Void.class && response.getBody() != null) {
            if (type instanceof Class && Entity.class.isAssignableFrom((Class) type)  ) {
                return (T) util.deserialize(response.getBody(), type);
            } else {
                //return (T) util.get(C8SerializationFactory.Serializer.CUSTOM).deserialize(response.getBody(), type);
                System.out.println("");
                return (T) null;
            }
        } else {
            return (T) null;
        }
    }


    private Response buildResponse(HttpResponse httpResponse) throws IOException {

            Response response = new Response();
            response.setResponseCode(httpResponse.getStatusLine().getStatusCode());

            final String content = IOUtils.toString(httpResponse.getEntity().getContent());
            if (!content.isEmpty()) {
                try {
                    response.setBody(
                            util.serialize(content, new C8Serializer.Options().stringAsJson(true).serializeNullValues(true)));
                } catch (C8DBException e) {
                    final byte[] contentAsByteArray = content.getBytes();
                    if (contentAsByteArray.length > 0) {
                        response.setBody(new VPackSlice(contentAsByteArray));
                    }
                }
            }
            return  response;
    }



    private HttpResponse buildBasicHttpResponse(String rawHttpResponse) throws IOException {


        // Create a new Apache BasicHttpResponse object
        BasicHttpResponse httpResponse = new BasicHttpResponse(null, 0, null);

        // Split raw HTTP response string into status line and header/body sections
        String[] responseSections = rawHttpResponse.split("\r\n\r\n", 2);
        String statusLine = responseSections[0];
        String headersAndBody = responseSections[1];

        // Set status line
        String[] statusLineParts = statusLine.split(StringUtils.SPACE, 3);
        httpResponse.setStatusLine(HttpVersion.HTTP_1_1, Integer.parseInt(statusLineParts[1]), statusLineParts[2]);

        // Set headers
        String[] headers = headersAndBody.split("\r\n");
        for (String header : headers) {
            String[] headerParts = header.split(":", 2);
            httpResponse.addHeader(headerParts[0], headerParts[1].trim());
        }

        // Set entity content
        StringEntity entity = new StringEntity(headersAndBody, StandardCharsets.UTF_8);
        httpResponse.setEntity(entity);


        return httpResponse;
    }

    public List<Response> geResponseList() {
        return responses;
    }
}
