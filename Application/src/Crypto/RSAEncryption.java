package Crypto;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAEncryption {    //For Message Encrypted
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private Cipher cipher;

    public RSAEncryption() {
        System.out.println("RSA Encryption Start");
    }

    public void generateKeyPair() throws NoSuchAlgorithmException, NoSuchPaddingException {
        //Generate RSA SK, PK
        SecureRandom random = new SecureRandom();
        KeyPairGenerator keypairgen = KeyPairGenerator.getInstance("RSA");
        keypairgen.initialize(2048, random);
        KeyPair keyPair = keypairgen.generateKeyPair();
        privateKey = (RSAPrivateKey) keyPair.getPrivate();
        publicKey = (RSAPublicKey) keyPair.getPublic();

        //Create Cipher Instance
        cipher = Cipher.getInstance("RSA");
    }

    //Encrypt with Sender's privateKey
    public byte[] RSAEncrypt(String msg) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptBytes = cipher.doFinal(msg.getBytes());
        System.out.println(new String(encryptBytes));
        return encryptBytes;
    }

    //Decrypte with Sender's publicKey
    public String RSADecrypt(byte[] encryptBytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        System.out.println(new String(decryptBytes));
        return new String(decryptBytes);
    }

}
