package net.anax.appServerClient.client.util;

public class StringUtilities {
    public static boolean isInteger(String string){
        if(string == null){return false;}
        try{
            Integer.parseInt(string);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }
    public static boolean isLong(String string){
        if(string == null){return false;}
        try{
            Long.parseLong(string);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

    public static boolean isBoolean(String bool){
        if(bool == null){return false;}
        if(bool.equalsIgnoreCase("true") || bool.equalsIgnoreCase("false")){return true;}
        return false;
    }
}
