package net.anax.client.util;

import net.anax.client.cryptography.KeyManager;
import net.anax.client.data.RequestFailedException;
import net.anax.client.http.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtilities {
    public static JSONObject doRequest(String url, JSONObject data, String authToken) throws RequestFailedException, HttpErrorStatusException {
        try {
            HttpRequest request = new HttpRequest(new URL(url), HttpMethod.GET);
            request.setPayload(data.toJSONString());
            request.addHeader(HttpHeader.Authorization, authToken);

            HttpWrapperRequest wrapperRequest = new HttpWrapperRequest(request, KeyManager.getINSTANCE().getAesKey(), KeyManager.getINSTANCE().getRSAPublicKey());
            HttpResponse response = wrapperRequest.send();

            if(response.responseCode != 200){
                throw new HttpErrorStatusException("did not receive 200 OK", response.responseCode, response.message);
            }

            JSONParser parser = new JSONParser();
            JSONObject responseData = (JSONObject) parser.parse(response.getPayload());
            return responseData;
        } catch (MalformedURLException e) {
            throw new RequestFailedException("invalid url", e);
        } catch (IOException e) {
            throw new RequestFailedException("connection failed", e);
        } catch (ParseException e) {
            throw new RequestFailedException("could not parse response payload", e);
        }

    }

    public static void validateSuccess(JSONObject data) throws RequestFailedException{
        if(!data.containsKey("success") || !(data.get("success") instanceof Boolean)){
            throw new RequestFailedException("the response does not contain valid success data");
        }
        if(!(Boolean)data.get("success")){
            throw new RequestFailedException("the request was not successful");
        }

    }

}
