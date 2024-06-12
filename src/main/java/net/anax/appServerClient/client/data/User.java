package net.anax.appServerClient.client.data;

import net.anax.appServerClient.client.http.HttpErrorStatusException;
import net.anax.appServerClient.client.server.RemoteServer;
import net.anax.appServerClient.client.server.Token;
import net.anax.appServerClient.client.util.HttpUtilities;
import net.anax.appServerClient.client.util.JsonUtilities;
import org.json.simple.JSONObject;

public class User {
    RemoteServer remoteServer;
    int id;
    public String cachedUsername = "";
    public String cachedName = "";
    public User(int id, RemoteServer remoteServer){
        this.id = id;
        this.remoteServer = remoteServer;
    }
    String requestUsername(Token token) throws RequestFailedException, HttpErrorStatusException {
        JSONObject data = new JSONObject();
        data.put("id", id);
        JSONObject response = HttpUtilities.doRequest(remoteServer.getUrl() + "/user/getUsername", data, token.getTokenString());
        if(!response.containsKey("username")){throw new RequestFailedException("response does not contain necessary data", null);}
        if(!(response.get("username") instanceof String)){throw new RequestFailedException("response does not contain valid data", null);}
        return (String) response.get("username");
    }
    public int getId(){return id;}
    String requestName(Token token) throws RequestFailedException, HttpErrorStatusException {
        JSONObject data = new JSONObject();
        data.put("id", id);
        JSONObject response = HttpUtilities.doRequest(remoteServer.getUrl() + "/user/getName", data, token.getTokenString());
        if(!response.containsKey("name")){throw new RequestFailedException("response does not contain necessary data");}
        if(!(response.get("name") instanceof String)){throw new RequestFailedException("response does not contain valid data");}
        return (String) response.get("name");
    }

    public JSONObject getJson(){
        JSONObject data = new JSONObject();
        data.put("username", cachedUsername);
        data.put("name", cachedName);
        data.put("id", id);
        return data;
    }

    public static User fromJson(JSONObject data, RemoteServer server) throws MissingDataException{
        MissingDataException e = new MissingDataException("missing data in json");
        String username = JsonUtilities.extractString(data, "username", e);
        String name = JsonUtilities.extractString(data, "name", e);
        int id = JsonUtilities.extractInt(data, "id", e);
        User u = new User(id,server);
        u.cachedName = name;
        u.cachedUsername = username;
        return u;
    }
    public void printSelf(){
        System.out.println("-------ClientUser printSelf start----------");
        System.out.println("id: " + id);
        System.out.println("Username: " + cachedUsername);
        System.out.println("Name: " + cachedName);
    }
}
