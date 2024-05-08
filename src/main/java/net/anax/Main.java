package net.anax;

import net.anax.client.cryptography.KeyManager;
import net.anax.client.data.RequestFailedException;
import net.anax.client.http.*;
import net.anax.client.server.RemoteServer;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Key;

public class Main {
    public static void main(String[] args) throws RequestFailedException, HttpErrorStatusException, ParseException, IOException {

        RequestFailedException.doPrintStackTrace = false;
        RemoteServer.getInstance().setUrl("https://dory-bright-endlessly.ngrok-free.app/");
        RemoteServer.getInstance().setUrl("http://localhost:8080");

        RemoteServer server = RemoteServer.getInstance();
        KeyManager keyManager = new KeyManager();

        HttpRequest request = new HttpRequest(new URL(server.getUrl()+"/user/login"), HttpMethod.POST);
        request.setPayload("{\"username\":\"username\", \"password\":\"password\"}");
        request.addHeader(HttpHeader.Authorization, "Bearer none");

        HttpWrapperRequest wrapperRequest = new HttpWrapperRequest(request, keyManager.getAesKey(), keyManager.getRSAPublicKey());
        HttpResponse response = wrapperRequest.send();
        response.printSelf();

    }
}