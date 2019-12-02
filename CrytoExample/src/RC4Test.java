import javax.crypto.spec.*;
import java.security.*;
import javax.crypto.*;

public class RC4Test {
    public RC4Test() {
        System.out.println("RC4 Encryption Start");
    }

    public byte[] encrypt(String msg, String key) throws Exception {
        SecureRandom sr = new SecureRandom(key.getBytes());
        KeyGenerator kg = KeyGenerator.getInstance("RC4");
        kg.init(sr);
        SecretKey sk = kg.generateKey();

        Cipher cipher = Cipher.getInstance("RC4");
        cipher.init(Cipher.ENCRYPT_MODE, sk);

        byte[] encrypted = cipher.doFinal(msg.getBytes());

        return encrypted;
    }

    public String decrypt(byte[] encryptedMsg, String key) throws Exception {
        SecureRandom sr = new SecureRandom(key.getBytes());
        KeyGenerator kg = KeyGenerator.getInstance("RC4");
        kg.init(sr);
        SecretKey sk = kg.generateKey();

        Cipher cipher = Cipher.getInstance("RC4");
        cipher.init(Cipher.DECRYPT_MODE, sk);
        byte[] decrpyted = cipher.doFinal(encryptedMsg);

        return new String(decrpyted);
    }
}
