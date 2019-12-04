package Crypto;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

/*
Copyright 2015 회사명 또는 사용자 명

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

public class CliAES {    //For Movie Encryption, AES256 - 키가 256bit, 32바이트이어야 함.
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

    public String createRandomKey() {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            SecureRandom random = new SecureRandom();
            generator.init(128, random);
            Key secureKey = generator.generateKey();
            return Base64.encodeBase64String(secureKey.getEncoded());
        } catch(NoSuchAlgorithmException nse) {
            nse.printStackTrace();
            return null;
        }
    }

    public void modeEncrypt() throws GeneralSecurityException{
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
    }

    public void modeDecrypt() throws GeneralSecurityException{
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
    }

    public byte[] AESEncrypt(byte[] msg, int len){
        byte[] encryptBytes = cipher.update(msg, 0, len);
        return encryptBytes;
    }

    public byte[] AESDecrypt(byte[] str, int len){
        byte[] decrpytBytes = cipher.update(str, 0, len);
        return decrpytBytes;
    }

    public String msgAESEncrypt(String msg) throws UnsupportedEncodingException{
        byte[] encryptBytes = null;
        String enStr = "";
        try {
            encryptBytes = cipher.doFinal(msg.getBytes("UTF-8"));
            enStr = new String(Base64.encodeBase64(encryptBytes));
        } catch(GeneralSecurityException gse) {
            gse.printStackTrace();
        }
        return enStr;
    }

    public String msgAESDecrypt(String str) throws UnsupportedEncodingException{
        try {
            byte[] byteStr = Base64.decodeBase64(str.getBytes());
            return new String(cipher.doFinal(byteStr), "UTF-8");
        } catch(GeneralSecurityException gse) {
            return null;
        }
    }

    public Cipher getCipher() {
        return cipher;
    }
}
