package net.anax.appServerClient;

import net.anax.appServerClient.client.data.ClientUser;
import net.anax.appServerClient.client.data.RequestFailedException;
import net.anax.appServerClient.client.http.*;
import net.anax.appServerClient.client.server.RemoteServer;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws RequestFailedException, HttpErrorStatusException, ParseException, IOException {

        RequestFailedException.doPrintStackTrace = false;
        RemoteServer.getInstance().setUrl("https://dory-bright-endlessly.ngrok-free.app/");
        RemoteServer.getInstance().setUrl("http://localhost:8080");

        RemoteServer server = RemoteServer.getInstance();

        ClientUser user = ClientUser.login("username", "password", server);
        user.requestUserInfo();
        user.printSelf();

    }
}