package common;

import java.io.UnsupportedEncodingException;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class ServerAES {    //For Movie Encryption, AES256 - 키가 256bit, 32바이트이어야 함.
    private String iv;
    private Key keySpec;

    private Cipher cipher;


    /*
     * 32자리의 키값 입력하여 객체 생성.
     * @param key 암/복호화 위한 키값.
     * @throws UnsupportedEncodingException 키 값의 길이가 16 이하일 경우 발생
     */

    public void createKey(String key) throws UnsupportedEncodingException{
        this.iv = key.substring(0,16);
        byte[] keyBytes = new byte[32];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if(len>keyBytes.length) {
            len = keyBytes.length;
        }
        System.arraycopy(b, 0, keyBytes, 0, len);
        this.keySpec = new SecretKeySpec(keyBytes, "AES");
    }

    public void modeDecrypt() throws GeneralSecurityException{
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
    }

    public byte[] AESDecrypt(byte[] str, int len){
        byte[] decrpytBytes = cipher.update(str, 0, len);
        return decrpytBytes;
    }

    public Cipher getCipher() {
        return cipher;
    }
}
