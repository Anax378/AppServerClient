package net.anax.client.data;

import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class MemoryManager {
    public static MemoryManager MainManager = null;

    User client;
    HashMap<Integer, User> rememberedUsers = new HashMap<>();
    HashMap<Integer, Task> rememberedTasks = new HashMap<>();
    HashMap<Integer, Group> rememberedGroups = new HashMap<>();

    public MemoryManager(User client){
        this.client = client;
        rememberedUsers.put(client.id, client);
    }

}
