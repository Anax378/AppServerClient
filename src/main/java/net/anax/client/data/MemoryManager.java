package net.anax.client.data;

import net.anax.client.http.HttpErrorStatusException;
import net.anax.client.server.RemoteServer;
import net.anax.client.util.JsonUtilities;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class MemoryManager {

    public static MemoryManager MainManager = null;

    ClientUser client;
    HashMap<Integer, User> rememberedUsers = new HashMap<>();
    HashMap<Integer, Task> rememberedTasks = new HashMap<>();
    HashMap<Integer, Group> rememberedGroups = new HashMap<>();

    public static void createNewMainMemoryManager(ClientUser client){
        MainManager = new MemoryManager(client);
    }
    public MemoryManager(ClientUser client){
        this.client = client;
    }
    public JSONObject getJson(){
        JSONArray users = new JSONArray();
        JSONArray tasks = new JSONArray();
        JSONArray groups = new JSONArray();

        for(User user : rememberedUsers.values()){
            users.add(user.getJson());
        }
        for(Task task : rememberedTasks.values()){
            tasks.add(task.getJSON());
        }
        for(Group group : rememberedGroups.values()){
            groups.add(group.getJSON());
        }

        JSONObject data = new JSONObject();
        data.put("users", users);
        data.put("tasks", tasks);
        data.put("groups", groups);
        data.put("clientUser", client.getJson());
        return data;
    }
    public static MemoryManager fromJson(JSONObject data, RemoteServer server) throws MissingDataException{
        MissingDataException e = new MissingDataException("missing data in json");
        JSONArray users = JsonUtilities.extractJSONArray(data, "users", e);
        JSONArray tasks = JsonUtilities.extractJSONArray(data, "tasks", e);
        JSONArray groups = JsonUtilities.extractJSONArray(data, "groups", e);
        ClientUser client = ClientUser.fromJson(JsonUtilities.extractJSONObject(data, "clientUser", e), server);


        MemoryManager manager = new MemoryManager(client);

        for(int i = 0; i < users.size(); i++){
            ClientUser user = ClientUser.fromJson(JsonUtilities.extractJSONObjectFromJSONArray(users, i, e), server);
            manager.rememberedUsers.put(user.id, user);
        }
        for(int i = 0; i < tasks.size(); i++){
            Task task = Task.getFromJSON(JsonUtilities.extractJSONObjectFromJSONArray(users, i, e));
            manager.rememberedTasks.put(task.id, task);
        }
        for(int i = 0; i < groups.size(); i++){
            Group group = Group.getFromJSON(JsonUtilities.extractJSONObjectFromJSONArray(users, i, e));
            manager.rememberedGroups.put(group.id, group);
        }

        return manager;
    }
    public User getUser(int id) throws RequestFailedException, HttpErrorStatusException {
        User user = new User(id, client.remoteServer);
        try{
            user.requestName(client.getToken());
            user.requestUsername(client.getToken());
        } catch (RequestFailedException | HttpErrorStatusException e) {
            if(rememberedUsers.containsKey(id)){
                return rememberedUsers.get(id);
            }else{
                throw e;
            }
        }

        rememberedUsers.put(id, user);
        return user;
    }
    public ClientUser getClient(){
        try{
            client.requestUserInfo();
        } catch (RequestFailedException | HttpErrorStatusException e) {
            return client;
        }
        return client;
    }
    public Group getGroup(int id) throws RequestFailedException, HttpErrorStatusException {
        Group group = new Group(id);
        try{
            group.requestGetGroup(client.getToken(), client.remoteServer);
        } catch (RequestFailedException | HttpErrorStatusException e) {
            if(rememberedGroups.containsKey(id)){
                return rememberedGroups.get(id);
            }else{
                throw e;
            }
        }
        rememberedGroups.put(id, group);
        return group;
    }

    public Task getTask(int id) throws RequestFailedException, HttpErrorStatusException {
        Task task = new Task(id);
        try{
            task.requestGetTask(client.getToken(), client.remoteServer);
        } catch (RequestFailedException | HttpErrorStatusException e) {
            if(rememberedTasks.containsKey(id)){
                return rememberedTasks.get(id);
            }
            else{
                throw e;
            }
        }

        rememberedTasks.put(id, task);
        return task;
    }
}
