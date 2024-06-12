package net.anax.appServerClient.client.cryptography;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class AESKey {
    private byte[] keyData;
    private Key key;

    public AESKey(byte[] keyData){
        this.keyData = keyData;
        this.key = new SecretKeySpec(keyData, "AES");
    }
    public byte[] getKeyData(){return keyData;}
    public Key getKey(){return key;}
}
