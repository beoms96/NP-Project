public class CryptoTest {   //암호 키 만드는 클래스에서 같은 객체로 계속 생성해주면 키 안겹칠듯
    public static void main(String[] args) throws Exception {
        System.out.println("AES=================================================================");
        String key = "12345678901234567890123456789012345";
        AESEncryption aes = new AESEncryption(key);  //key는 32자 이상
        String plainText = "안녕 자바";
        String cipherText = aes.AESEncrypt(plainText);
        System.out.println(cipherText);
        String getPlain = aes.AESDecrypt(cipherText, key);
        System.out.println(getPlain);

        /*System.out.println("RSA=================================================================");

        RSAEncryption rsaEx1 = new RSAEncryption();
        rsaEx1.generateKeyPair();
        System.out.println(new String(rsaEx1.RSAEncrypt(plainText)));
        System.out.println(rsaEx1.RSADecrypt(rsaEx1.RSAEncrypt(plainText)));

        System.out.println("RSA2=================================================================");

        RSAEx2 rsaEx2 = new RSAEx2();
        HashMap<String, String> rsaKeyPair = rsaEx2.createKeyPairAsString();

        String publicKey = rsaKeyPair.get("publicKey");
        String privateKey = rsaKeyPair.get("privateKey");

        System.out.println("만들어진 공개키:" + publicKey);
        System.out.println("만들어진 개인키:" + privateKey);

        plainText = "플레인 텍스트";
        System.out.println("평문: " + plainText);

        String encryptedText = rsaEx2.encode(plainText, publicKey);
        System.out.println("암호화: " + encryptedText);

        String decryptedText = rsaEx2.decode(encryptedText, privateKey);
        System.out.println("복호화: " + decryptedText);

        System.out.println("UUID=================================================================");

        for(int i=0;i<10;i++) { //32자리 16진수
            System.out.println(UUID.randomUUID().toString().replace("-","")); //UUID
            System.out.println(new java.rmi.dgc.VMID());    //VMID
        }
        UUID uid = new UUID(1,2);
        System.out.println(uid.toString());
        UUID uid2 = new UUID(0xaaaa, 0xffff);
        System.out.println(uid2.toString());
        UUID one = UUID.randomUUID();
        System.out.println("UUID One: " + one.toString());*/

        /* UUID
         *1. 업로드된 파일명의 중복을 방지하기 위해 파일명을 변경할 때 사용.
         *2. 첨부파일 파일다운로드시 다른 파일을 예측하여 다운로드하는것을 방지하는데 사용.
         *3. 일련번호 대신 유추하기 힘든 식별자를 사용하여 다른 컨텐츠의 임의 접근을 방지하는데 사용.
         * */

        System.out.println("RC4=================================================================");
        RC4Test rc4 = new RC4Test();

        plainText = "플레인 텍스트";
        System.out.println("평문: " + plainText);

        byte[] encryptedText1 = rc4.encrypt(plainText, key);
        System.out.println("암호화: " + new String(encryptedText1));

        String decryptedText1 = rc4.decrypt(encryptedText1, key);
        System.out.println("복호화: " + decryptedText1);

    }

}
