package net.anax.appServerClient.client.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    HttpURLConnection connection;
    URL url;
    String payload;
    HttpMethod method;
    HashMap<HttpHeader, String> headers = new HashMap<>();

    public HttpRequest(URL url, HttpMethod method){
        this.url = url;
        this.method = method;
    }

    public HttpRequest addHeader(HttpHeader header, String value){
        headers.put(header, value);
        return this;
    }

    public HttpRequest setPayload(String payload){
        this.payload = payload;
        return this;
    }

    public HttpResponse send() throws IOException {
        this.connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod(method.name());

        for(HttpHeader header : headers.keySet()){
            connection.setRequestProperty(header.key, headers.get(header));
        }

        if(payload != null && !payload.isEmpty()){
            connection.setDoOutput(true);
            OutputStream ostream = connection.getOutputStream();
            ostream.write(payload.getBytes(StandardCharsets.US_ASCII));
        }

        HttpResponse response = new HttpResponse(connection.getResponseMessage(), connection.getResponseCode());

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder builder = new StringBuilder();

        while((line = reader.readLine()) != null){
            builder.append(line);
        }

        response.setPayload(builder.toString());

        Map<String, List<String>> headerField = connection.getHeaderFields();
        for(String header : headerField.keySet()){
            StringBuilder headerValueBuilder = new StringBuilder();
            for(String value : headerField.get(header)){
                headerValueBuilder.append(value).append("; ");
            }
            headerValueBuilder.delete(headerValueBuilder.length()-2, headerValueBuilder.length());
            response.addHeader(header, headerValueBuilder.toString());
            headerValueBuilder.setLength(0);
        }

        return response;
    }
}
