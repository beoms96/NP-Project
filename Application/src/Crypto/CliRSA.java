package Crypto;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;

public class CliRSA {
    static final int KEY_SIZE = 1024;

    public HashMap<String, String> createKeyPairAsString() {
        HashMap<String, String> stringKeyPair = new HashMap<>();
        try {
            SecureRandom secureRandom = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEY_SIZE, secureRandom);
            KeyPair keyPair = keyPairGenerator.genKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            String stringPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String stringPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            stringKeyPair.put("publicKey", stringPublicKey);
            stringKeyPair.put("privateKey", stringPrivateKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringKeyPair;
    }

}
