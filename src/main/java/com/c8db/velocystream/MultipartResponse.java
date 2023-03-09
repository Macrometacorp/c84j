package com.c8db.velocystream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MultipartResponse extends Response {

    private static void parseMultipartResponse( InputStream responseStream) throws IOException {




//        MultipartStream multipartStream = new MultipartStream(
//                new ByteArrayInputStream(responseStream.readAllBytes()),
//                "XXXpartXXX".getBytes(),
//                1024,    // internal buffer size (you choose)
//                null);   // progress indicator (none)
//
//        boolean nextPart = multipartStream.skipPreamble();
//        while (nextPart) {
//
//            ByteArrayOutputStream output = new ByteArrayOutputStream();
//            String partHeaders = multipartStream.readHeaders();
//            multipartStream.readBodyData(output);
//
//            System.out.println("Headers: " + partHeaders);
//            System.out.println("Body: " + output.toString("UTF-8"));
//
//            // do something with the multi-line part headers
//            // do something with the part 'output' byte array
//
//            nextPart = multipartStream.readBoundary();
//        }
    }
}
