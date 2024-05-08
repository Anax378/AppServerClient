package net.anax.client.cryptography;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAPublicKey {
    private byte[] keyData;
    PublicKey key;

    public RSAPublicKey(byte[] keyData){
        try {
            this.keyData = keyData;
            this.key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyData));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public PublicKey getKey(){return key;}

    public byte[] getKeyData() {return keyData;}
}
