package net.anax.appServerClient.client.http;

public enum HttpHeader {
    Authorization("Authorization"),
    ContentType("Content-Type"),
    ContentLength("Content-Length"),

    ;
    public String key;
    HttpHeader(String key){
        this.key = key;
    }

}
