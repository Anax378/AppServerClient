package net.anax.appServerClient.client.data;

import net.anax.appServerClient.client.util.JsonUtilities;
import org.json.simple.JSONObject;

public class TaskAssignment {
    public int taskId;
    public int userId;
    public boolean isCompleted;
    public TaskAssignment(int taskId, int userId, boolean isCompleted) {
        this.taskId = taskId;
        this.userId = userId;
        this.isCompleted = isCompleted;
    }

    public JSONObject getJson(){
        JSONObject data = new JSONObject();
        data.put("taskId", taskId);
        data.put("userId", userId);
        data.put("isCompleted", isCompleted);
        return data;
    }
    public static TaskAssignment getFromJson(JSONObject data) throws MissingDataException {
        MissingDataException e = new MissingDataException("missing data");

        int taskId = JsonUtilities.extractInt(data, "taskId", e);
        int userId = JsonUtilities.extractInt(data, "userId", e);
        boolean isCompleted = JsonUtilities.extractBoolean(data, "isCompleted", e);
        return new TaskAssignment(taskId, userId, isCompleted);
    }
    public void printSelf() {
        System.out.println("task id: " + taskId);
        System.out.println("user id: " + userId);
        System.out.println("is completed: " + isCompleted);
    }
}
