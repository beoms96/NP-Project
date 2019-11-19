package Crypto;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAEncryption {    //For Message Encrypted
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private String privateKey1;
    private String pulbicKey1;
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

        privateKey1 = new String(byteArrayToHex(publicKey.getEncoded()));
        pulbicKey1 = new String(byteArrayToHex(privateKey.getEncoded()));

        //Create Cipher Instance
        cipher = Cipher.getInstance("RSA");
    }

    //Encrypt with Sender's privateKey
    public byte[] RSAEncrypt(String msg) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptBytes = cipher.doFinal(msg.getBytes("UTF-8"));
        return encryptBytes;
    }

    //Decrypte with Sender's publicKey
    public String RSADecrypt(byte[] encryptBytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }

    //RSA String to PK, SK
    public void String2PublicKey(String publicKeyStr) {
        KeyFactory keyFactory = null;
        PublicKey publicKey = null;

        try {
            X509EncodedKeySpec ukeySpec = new X509EncodedKeySpec(hexToByteArray(publicKeyStr));
            keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(ukeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void String2PrivateKey(String privateKeyStr) {
        try {
            PKCS8EncodedKeySpec rkeySpec = new PKCS8EncodedKeySpec(hexToByteArray(privateKeyStr));
            KeyFactory rkeyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = null;
            privateKey = rkeyFactory.generatePrivate(rkeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] hexToByteArray(String hex) {
        if(hex==null||hex.length() == 0) {
            return null;
        }
        byte[] ba = new byte[hex.length() / 2];
        for(int i=0;i<ba.length;i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        }
        return ba;
    }

    public String byteArrayToHex(byte[] ba) {
        if(ba==null||ba.length==0) {
            return null;
        }
        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber = "";
        for(int x=0;x<ba.length;x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);
            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }

        return sb.toString();
    }

}

