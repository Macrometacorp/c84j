package com.c8db.velocystream;

import com.arangodb.velocypack.VPackSlice;
import org.apache.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.SPACE;

public class MultipartRequest extends Request{

    public static final String BOUNDARY = "C8_BATCH_QUERY_PART";
    public static final String BATCH_CONTENT_TYPE = "multipart/form-data; boundary=C8_BATCH_QUERY_PART";
    public static final String ARANGO_BATCHPART = "application/x-arango-batchpart";
    private static final String LINE_FEED = "\n";
    List<Request> requestParts = new ArrayList<>();



    public MultipartRequest(String tenant, String database) {
        super(tenant, database, RequestType.POST, "/_api/batch");
    }

    @Override
    public VPackSlice getBody() {
        throw new UnsupportedOperationException("VPack not supported");
    }


    public Request setBody(final VPackSlice body) {
        throw new UnsupportedOperationException("VPack not supported");
    }

    public void addPart(Request request) {
        requestParts.add(request);
    }

    public List<Request> getParts() {
        return requestParts;
    }

    public String buildRequestBody(){
        StringBuilder builder = new StringBuilder();

        for(Request request : requestParts){
            builder.append("--".concat(BOUNDARY)
                    .concat(LINE_FEED));
            builder.append("Content-Type: "
                    .concat(ARANGO_BATCHPART)
                    .concat(LINE_FEED));
            builder.append(LINE_FEED);
            builder.append(request.getRequestType().name()
                    .concat(SPACE)
                    .concat(request.getRequest())
                    .concat(LINE_FEED));
            builder.append(LINE_FEED);
            if(null != request.getBody()){
                builder.append(request.getBody().toString());
            }
            builder.append(LINE_FEED);
        }
        builder.append("--".concat(BOUNDARY).concat("--"));
        return builder.toString();
    }
}
