package net.anax.appServerClient.client.server;

import net.anax.appServerClient.client.data.ID;
import net.anax.appServerClient.client.data.MissingDataException;
import net.anax.appServerClient.client.util.JsonUtilities;
import net.anax.appServerClient.client.util.StringUtilities;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Base64;

public class Token {
    public static final Token EMPTY = new Token("", ID.UNKNOWN.id, 0L);
    String tokenString;
    public int subject;
    public long expirationTimestamp;

    public Token(String tokenString, int subject, long expirationTimestamp){
        this.subject = subject;
        this.tokenString = tokenString;
        this.expirationTimestamp = expirationTimestamp;
    }

    public JSONObject getJson(){
        JSONObject data = new JSONObject();
        data.put("tokenString", tokenString);
        data.put("subject", subject);
        data.put("expirationTimestamp", expirationTimestamp);
        return data;
    }

    public static Token getFromJSON(JSONObject data) throws MissingDataException {
        MissingDataException e = new MissingDataException("insufficient data in json");
        String tokenString = JsonUtilities.extractString(data, "tokenString", e);
        int subject = JsonUtilities.extractInt(data, "subject", e);
        long exp = JsonUtilities.extractInt(data, "expirationTimestamp", e);
        return new Token(tokenString, subject, exp);
    }
    public static Token parseToken(String tokenString){
        String[] parts = tokenString.split("\\.");
        if(parts.length != 3){return null;}

        String bodyString = new String(Base64.getDecoder().decode(parts[1]));

        JSONParser parser = new JSONParser();
        JSONObject body;
        try {
            body = (JSONObject) parser.parse(bodyString);
        } catch (ParseException e) {
            return null;
        }

        if(!body.containsKey("sub") || !body.containsKey("exp") || !StringUtilities.isLong((String) body.get("exp"))){return null;}

        return new Token(tokenString, Integer.parseInt((String) body.get("sub")), Long.parseLong((String) body.get("exp")));
    }

    public String getTokenString(){return tokenString;}


}
