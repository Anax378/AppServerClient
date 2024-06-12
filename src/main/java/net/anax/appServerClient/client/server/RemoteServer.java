package net.anax.appServerClient.client.server;

public class RemoteServer {
    private String url;
    static RemoteServer REMOTE_SERVER;
    public RemoteServer(String url){
        this.url = url;
    }
    public String getUrl(){
        return url;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public static RemoteServer getInstance(){
        if(REMOTE_SERVER == null){
            REMOTE_SERVER = new RemoteServer(null);
        }
        return REMOTE_SERVER;
    }
}
