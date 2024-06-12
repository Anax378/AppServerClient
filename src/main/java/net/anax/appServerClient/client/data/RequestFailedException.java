package net.anax.appServerClient.client.data;

public class RequestFailedException extends Exception{
    public static boolean doPrintStackTrace = false;
    public RequestFailedException(String message){
        this(message, null);
    }
    public RequestFailedException(String message, Exception e){
        super(message);
        if(doPrintStackTrace){
            this.printStackTrace();
            if(e != null){
                e.printStackTrace();
            }
        }
    }
}
