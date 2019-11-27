package Crypto;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import org.apache.commons.codec.binary.Base64;

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

public class RSAManager {
    /*서버에서 공개키 개인키 새로 발급해 PublicKey.txt, PrivateKey.txt 파일 생성
     * 공개키, 개인키는 실제 클라이언트에게 전달되었고, 공개키는 외부에 공개되어 있다 가정
     * 외부에서 공개키 이용해 평문1 암호화해서 전달되었음.
     * 공개키로 암호화된 암호화문을 개인키로 복호화해서 평문2 얻음
     * 같은지 확인
     */

    public static void main(String[] args) {
        System.out.println("Server start-------------------------------");
        //서버 측 키 파일 생성
        PublicKey publicKey1 = null;
        PrivateKey privateKey1 = null;

        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(512, secureRandom);

            KeyPair keyPair = keyPairGenerator.genKeyPair();
            publicKey1 = keyPair.getPublic();
            privateKey1 = keyPair.getPrivate();

            KeyFactory keyFactory1 = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec rsaPublicKeySpec = keyFactory1.getKeySpec(publicKey1, RSAPublicKeySpec.class);
            RSAPrivateKeySpec rsaPrivateKeySpec = keyFactory1.getKeySpec(privateKey1, RSAPrivateKeySpec.class);
            System.out.println("Public key modules: " + rsaPublicKeySpec.getModulus());
            System.out.println("Public key exponents: " + rsaPublicKeySpec.getPublicExponent());
            System.out.println("Private key modules: " + rsaPrivateKeySpec.getModulus());
            System.out.println("Private key exponents: " + rsaPrivateKeySpec.getPrivateExponent());
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch(InvalidKeySpecException e) {
            e.printStackTrace();
        }

        byte[] bPublicKey1 = publicKey1.getEncoded();
        String sPublicKey1 = Base64.encodeBase64String(bPublicKey1);

        byte[] bPrivateKey1 = privateKey1.getEncoded();
        String sPrivateKey1 = Base64.encodeBase64String(bPrivateKey1);

        try {
            BufferedWriter bw1 = new BufferedWriter(new FileWriter("PublicKey.txt"));
            bw1.write(sPublicKey1);
            bw1.newLine();
            bw1.close();
            BufferedWriter bw2 = new BufferedWriter(new FileWriter("PrivateKey.txt"));
            bw2.write(sPrivateKey1);
            bw2.newLine();
            bw2.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        //클라이언트 측 키 파일 로딩
        System.out.println("Client Start-------------------------------");
        String sPublicKey2 = null;
        String sPrivateKey2 = null;

        BufferedReader brPublicKey = null;
        BufferedReader brPrivateKey = null;

        try{
            brPublicKey = new BufferedReader(new FileReader("PublicKey.txt"));
            sPublicKey2 = brPublicKey.readLine();
            brPrivateKey = new BufferedReader(new FileReader("PrivateKey.txt"));
            sPrivateKey2 = brPrivateKey.readLine();
            System.out.println("Public Key & Private Key Read");
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(brPublicKey!=null)
                    brPublicKey.close();
                if(brPrivateKey!=null)
                    brPrivateKey.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        byte[] bPublicKey2 = Base64.decodeBase64(sPublicKey2.getBytes());
        PublicKey publicKey2 = null;

        byte[] bPrivateKey2 = Base64.decodeBase64(sPrivateKey2.getBytes());
        PrivateKey privateKey2 = null;

        try {
            KeyFactory keyFactory2 = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bPublicKey2);
            publicKey2 = keyFactory2.generatePublic(publicKeySpec);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bPrivateKey2);
            privateKey2 = keyFactory2.generatePrivate(privateKeySpec);
        } catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        String sPlain1 = "Welcome to RSA";
        String sPlain2 = null;

        try {
            Cipher cipher = Cipher.getInstance("RSA");

            cipher.init(Cipher.ENCRYPT_MODE, publicKey2);
            byte[] bCipher1 = cipher.doFinal(sPlain1.getBytes());
            String sCipherBase64 = Base64.encodeBase64String(bCipher1);

            byte[] bCipher2 = Base64.decodeBase64(sCipherBase64.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, privateKey2);
            byte[] bPlain2 = cipher.doFinal(bCipher2);
            sPlain2 = new String(bPlain2);
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (NoSuchPaddingException e){
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch(BadPaddingException e) {
            e.printStackTrace();
        }

        System.out.println("sPlain1: " + sPlain1);
        System.out.println("sPlain2: " + sPlain2);
    }
}
