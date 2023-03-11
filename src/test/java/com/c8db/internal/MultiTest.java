package com.c8db.internal;

import com.c8db.C8Collection;
import com.c8db.C8DB;
import com.c8db.C8Database;
import com.c8db.velocystream.Request;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MultiTest extends InternalC8Database<C8DBImpl, C8ExecutorSync>{


    protected MultiTest(C8DBImpl c8db, String tenant, String name, String spotDc, String dcList) {
        super(c8db, tenant, name, spotDc, dcList);
    }

    private static C8DB getC8DB(String url, int port, String email, String password) throws MalformedURLException, MalformedURLException {
        C8DB.Builder clusterBuilder = new C8DB.Builder()
                .host(url, port)
                .useSsl(true)
                .email(email)
                .password(password);
        return clusterBuilder.build();
    }

    public static void main(String[] args) throws MalformedURLException {

        db();

    }

    static void db1() throws MalformedURLException {
        String url = "api-ashish-sin.eng.macrometa.io";//"api-local-ap-west.eng.macrometa.io", "api-ashish-sin.eng.macrometa.io";
        int port = 443;
        String email = "service@macrometa.io";//""mm@macrometa.io";
        String password = "4d6a55324f44677a4d7a4059314e475a684f4459794d54466a4f";//""Macrometa123!@#";//Base64.getEncoder().encodeToString("Macrometa123!@#".getBytes(StandardCharsets.UTF_8)); ;
        C8DB c8db = getC8DB(url, port, email, password);

        new  MultiTest((C8DBImpl) c8db, "_mm", "_system", "", "");
        System.out.println("4d6a55324f44677a4d7a4059314e475a684f4459794d54466a4f".equals(Base64.getEncoder().encodeToString("Macrometa123!@#".getBytes(StandardCharsets.UTF_8))));
        //System.out.println("hello" + c8db.db().exists());
    }

    static void db(){
        C8DB.Builder clusterBuilder = new C8DB.Builder()
                .host("api-local-ap-west.eng.macrometa.io", Integer.valueOf(443))
                .useSsl(Boolean.valueOf(true))
                .apiKey("multi.FGsAImsHZOuguJEyxAnU8zvQDWEwA6oFXrj7csW8QmB65dNe8NeLpU6xO2wZHZ4g380674");



        C8Database c8database = clusterBuilder.build().db("_mm", "_system");
        System.out.println("c8database " + c8database.exists());

        MultiTest multiTest = new  MultiTest((C8DBImpl) clusterBuilder.build(), "_mm", "_system", "", "");


//        String query = "FOR doc in @@collection FILTER doc.region==@region AND doc.service==@service AND doc.type==@type AND doc.entity==@entity AND doc.timestamp>@timestamp RETURN doc";
//        Map<String, Object> bindVars = new HashMap<>();
//        bindVars.put("product", "ram");
//        bindVars.put("@collection", "inventory");
//        bindVars.put("quantity", "12");
//        Request request = multiTest.queryRequest(query, bindVars, null);
//        request.putHeaderParam("Content-Type", "multipart/form-data; boundary=XXXpartXXX");

//        System.out.println("request " + request.getRequest());
//        System.out.println("RequestType " + request.getRequestType().name());
//        System.out.println("body: " + request.getBody().toString());


        //Request request = multiTest.batchQueryRequest();

        buildList(c8database);
    }

    static void buildList(C8Database c8database){

        List<String> qs = new ArrayList<>();
        List<Map<String, Object>> vs = new ArrayList<>();

        for(int i = 0 ; i<2;i++){
            String query = "FOR doc IN @@collection FILTER (doc.product == @product )  UPDATE doc WITH { quantity: @quantity } IN @@collection";
            Map<String, Object> bindVars = new HashMap<>();
            bindVars.put("@collection", "inventory");
            bindVars.put("product", "ram");
            //bindVars.put("quantity", String.valueOf(100));
            qs.add(query);
            vs.add(bindVars);
        }


        c8database.executeBatchQueries(qs, vs,  String.class);

    }
}
