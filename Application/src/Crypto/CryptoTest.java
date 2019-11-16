package Crypto;

public class CryptoTest {
    public static void main(String[] args) throws Exception {
        String key = "12345678901234567890123456789012345";
        AESEncryption aes = new AESEncryption(key);  //key는 32자 이상
        String plainText = "안녕 자바";
        String cipherText = aes.AESEncrypt(plainText);
        System.out.println(cipherText);
        String getPlain = aes.AESDecrypt(cipherText, key);
        System.out.println(getPlain);
    }
}
