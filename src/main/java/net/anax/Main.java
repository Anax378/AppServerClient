package net.anax;

import net.anax.client.data.Group;
import net.anax.client.data.RequestFailedException;
import net.anax.client.data.Task;
import net.anax.client.data.User;
import net.anax.client.http.HttpErrorStatusException;
import net.anax.client.server.RemoteServer;
import net.anax.client.util.JsonUtilities;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {
    public static void main(String[] args) throws RequestFailedException, HttpErrorStatusException, ParseException {

        RequestFailedException.doPrintStackTrace = false;
        RemoteServer.getInstance().setUrl("http://localhost:8080");
        RemoteServer server = RemoteServer.getInstance();
        User user = User.login("client", "password", RemoteServer.getInstance());
        user.requestUserInfo();
        user.printSelf();

        Group g = Group.requestGroupFromId(user.getToken(), user.cachedGroupIds.get(0), server);
        g.printSelf();

        Task task = Task.requestTaskFromId(user.getToken(), user.cachedTaskAssignments.get(0).taskId, server);
        task.printSelf();

    }
}