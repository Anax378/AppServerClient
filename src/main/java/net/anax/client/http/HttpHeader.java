package net.anax.client.http;

public enum HttpHeader {
    Authorization("Authorization"),
    ContentType("Content-Type"),

    ;
    public String key;
    HttpHeader(String key){
        this.key = key;
    }

}
