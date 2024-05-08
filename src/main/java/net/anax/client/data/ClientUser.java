package net.anax.client.data;

import net.anax.client.http.*;
import net.anax.client.server.RemoteServer;
import net.anax.client.server.Token;
import net.anax.client.util.HttpUtilities;
import net.anax.client.util.JsonUtilities;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class ClientUser extends User{
    private boolean autoRefreshToken = true;
    String password = "";
    Token cachedToken = Token.EMPTY;
    public ArrayList<TaskAssignment> cachedTaskAssignments = new ArrayList<>();
    public ArrayList<Integer> cachedGroupIds = new ArrayList<>();

    public void setDoAutoRefreshToken(boolean autoRefreshToken){
        this.autoRefreshToken = autoRefreshToken;
    }
    public ClientUser(int id, RemoteServer remoteServer){
        super(id, remoteServer);
    }
    public static ClientUser login(String username, String password, RemoteServer remoteServer) throws RequestFailedException, HttpErrorStatusException {
        ClientUser user = new ClientUser(-1, remoteServer);
        user.cachedUsername = username;
        user.password = password;
        user.cachedToken = user.requestToken();
        user.id = user.cachedToken.subject;
        return user;
    }
    public Token requestToken() throws RequestFailedException, HttpErrorStatusException {
        if(cachedUsername == null){throw new RequestFailedException("username is null, cannot request token", null);}
        if(password == null){throw new RequestFailedException("password is null, cannot request token", null);}

        JSONObject data = new JSONObject();
        data.put("username", cachedUsername);
        data.put("password", password);
        JSONObject response = HttpUtilities.doRequest(remoteServer.getUrl() + "/user/login", data, "none");
        if(!response.containsKey("token")){throw new RequestFailedException("response does not contain necessary data", null);}
        if(!(response.get("token") instanceof String)){throw new RequestFailedException("response does not contain valid data", null);}
        Token token = Token.parseToken((String) response.get("token"));
        if(token == null){throw new RequestFailedException("response token is not valid", null);}
        return token;
    }
    public Token getToken() throws RequestFailedException, HttpErrorStatusException {
        if(cachedToken == Token.EMPTY || cachedToken == null){
            cachedToken = requestToken();
        }
        if(autoRefreshToken){
            if(cachedToken.expirationTimestamp >= System.currentTimeMillis()){
                cachedToken = requestToken();
            }
        }
        return cachedToken;
    }
    public String getCachedUsername() throws RequestFailedException, HttpErrorStatusException {
        if(cachedUsername == null){
            cachedUsername = requestUsername();
        }
        return cachedUsername;
    }
    String requestUsername() throws RequestFailedException, HttpErrorStatusException {
        return super.requestUsername(getToken());
    }
    public String getCachedName() throws RequestFailedException, HttpErrorStatusException {
        if(cachedName == null){
            cachedName = requestName();
        }
        return cachedName;
    }
    String requestName() throws RequestFailedException, HttpErrorStatusException {
        return super.requestName(getToken());
    }
    public ClientUser requestUserInfo() throws RequestFailedException, HttpErrorStatusException {
        JSONObject data = new JSONObject();
        data.put("id", id);
        JSONObject responseData = HttpUtilities.doRequest(remoteServer.getUrl() + "/user/getUser", data, this.getToken().getTokenString());

        RequestFailedException e = new RequestFailedException("response does not contain necessary data");

        JSONArray taskIds = JsonUtilities.extractJSONArray(responseData, "taskIds", e);
        JSONArray isTaskDone = JsonUtilities.extractJSONArray(responseData, "isTaskDone", e);
        JSONArray groupIds = JsonUtilities.extractJSONArray(responseData, "groupIds", e);

        this.cachedUsername = JsonUtilities.extractString(responseData, "username", e);
        this.cachedName = JsonUtilities.extractString(responseData, "name", e);

        int[] taskIdsArray = new int[taskIds.size()];
        boolean[] isTaskDoneArray = new boolean[isTaskDone.size()];
        int[] groupIdsArray = new int[groupIds.size()];

        if(taskIdsArray.length != isTaskDoneArray.length){
            throw new RequestFailedException("response contains incomplete data");
        }

        for(int i = 0; i < taskIds.size(); i++){
            if(taskIds.get(i) instanceof Long){
                taskIdsArray[i] = ((Long) taskIds.get(i)).intValue();
            }else{
                throw new RequestFailedException("could not parse response data");
            }
        }

        for(int i = 0; i < isTaskDone.size(); i++){
            if(isTaskDone.get(i) instanceof Boolean){
                isTaskDoneArray[i] = (boolean) isTaskDone.get(i);
            } else{
                throw new RequestFailedException("could not parse response data");
            }
        }

        for(int i = 0; i < groupIds.size(); i++){
            if(groupIds.get(i) instanceof Long){
                groupIdsArray[i] = ((Long) groupIds.get(i)).intValue();
            }else{
                throw new RequestFailedException("could not parse response data");
            }
        }

        for(int i = 0; i < taskIdsArray.length; i++){
            cachedTaskAssignments.add(new TaskAssignment(taskIdsArray[i], this.id, isTaskDoneArray[i]));
        }
        for(int i = 0; i < groupIdsArray.length; i++){
            cachedGroupIds.add(groupIdsArray[i]);
        }

        return this;
    }
    public ClientUser changeName(String newName) throws RequestFailedException, HttpErrorStatusException {
        requestChangeName(newName);
        this.cachedName = newName;
        return this;
    }
    ClientUser requestChangeName(String newName) throws RequestFailedException, HttpErrorStatusException {
        JSONObject data = new JSONObject();
        data.put("id", id);
        data.put("newName", newName);
        JSONObject response = HttpUtilities.doRequest(remoteServer.getUrl() + "/user/setName", data, getToken().getTokenString());
        if (response.containsKey("success")){
            if(response.get("success") instanceof Boolean){
                if((Boolean)response.get("success")){
                    return this;
                }
            }
        }
        throw new RequestFailedException("the request was not successful");
    }
    public ClientUser changeUsername(String newUsername) throws RequestFailedException, HttpErrorStatusException {
        requestChangeUsername(newUsername);
        this.cachedUsername = newUsername;
        return this;
    }
    ClientUser requestChangeUsername(String newUsername) throws RequestFailedException, HttpErrorStatusException {
        JSONObject data = new JSONObject();
        data.put("id", id);
        data.put("newUsername", newUsername);
        JSONObject response = HttpUtilities.doRequest(remoteServer.getUrl() + "/user/setUsername", data, getToken().getTokenString());
        if (response.containsKey("success")){
            if(response.get("success") instanceof Boolean){
                if((Boolean)response.get("success")){
                    return this;
                }
            }
        }
        throw new RequestFailedException("the request was not successful");
    }
    public static ClientUser requestCreateUser(String username, String password, String name, RemoteServer remoteServer) throws RequestFailedException, HttpErrorStatusException {
        JSONObject data = new JSONObject();
        data.put("username", username);
        data.put("password", password);
        data.put("name", name);

        JSONObject response = HttpUtilities.doRequest(remoteServer.getUrl() + "/user/createUser", data, "none");

        if(!response.containsKey("id")){throw new RequestFailedException("response does not contain necessary data");}
        if(!(response.get("id") instanceof Long)){throw new RequestFailedException("response does not contain valid data");}
        int id = ((Long) response.get("id")).intValue();

        ClientUser user = new ClientUser(id, remoteServer);
        user.cachedUsername = username;
        user.cachedName = name;
        user.password = password;

        return user;
    }
    public JSONObject getJson(){
        JSONObject data = new JSONObject();
        data.put("id", id);
        data.put("password", password);
        data.put("username", cachedUsername);
        data.put("name", cachedName);
        data.put("token", cachedToken.getJson());

        JSONArray array = new JSONArray();

        for(TaskAssignment assignment : cachedTaskAssignments){
            array.add(assignment.getJson());
        }

        data.put("assignments", array);
        return data;

    }

    public static ClientUser fromJson(JSONObject data, RemoteServer server) throws MissingDataException{
        MissingDataException e = new MissingDataException("json is missing data");
        int id = JsonUtilities.extractInt(data, "id", e);
        String password = JsonUtilities.extractString(data, "password", e);
        String username = JsonUtilities.extractString(data, "username", e);
        String name = JsonUtilities.extractString(data, "name", e);
        Token token = Token.getFromJSON(JsonUtilities.extractJSONObject(data, "token", e));

        JSONArray assignments = JsonUtilities.extractJSONArray(data, "assignments", e);

        ClientUser user = new ClientUser(id, server);
        for(Object o : assignments){
            if(o instanceof JSONObject){
                user.cachedTaskAssignments.add(TaskAssignment.getFromJson((JSONObject) o));
            }
        }

        user.password = password;
        user.cachedUsername = username;
        user.cachedName = name;
        user.cachedToken = token;
        return user;
    }
    public void printSelf(){
        System.out.println("-------ClientUser printSelf start----------");
        System.out.println("id: " + id);
        System.out.println("password: " + password);
        System.out.println("Username: " + cachedUsername);
        System.out.println("Name: " + cachedName);
        if(cachedToken != null){
            System.out.println("Token: " + cachedToken.getTokenString());
        }else{
            System.out.println("Token: null");
        }
        for(TaskAssignment assignment : cachedTaskAssignments){
            assignment.printSelf();
        }
        System.out.println("Group Ids: " + Arrays.toString(cachedGroupIds.toArray()));
    }

}
