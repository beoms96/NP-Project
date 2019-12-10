package Streaming;

import ClientLogic.MultiClient;
import Crypto.CliAES;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class CrypRcvAudio implements Runnable{
    //Member
    private MultiClient mc;
    private AudioFormat audioFormat;
    private SourceDataLine sourceDataLine;
    private CliAES rcvcaes2;

    //Constructor
    public CrypRcvAudio(MultiClient mc, String key) {
        System.out.println(mc.getId() + "Audio Receiving Start");
        this.mc = mc;
        rcvcaes2 = new CliAES();
        try {
            rcvcaes2.createKey(key);
            rcvcaes2.modeDecrypt();
        } catch(UnsupportedEncodingException | GeneralSecurityException ue) { ue.printStackTrace(); }
    }

    //Method
    @Override
    public void run() {
        try{
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo1 = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo1);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            byte[] tempBuffer = null;
            byte[] decryptedData =null;
            while (!mc.getIsStop()) {
                int Dlength = mc.getAudiois().readInt();
                System.out.println(Dlength);
                if(Dlength == 0) {
                    break;
                }
                else {
                    tempBuffer = new byte[Dlength];
                    int cnt = mc.getAudiois().read(tempBuffer, 0, Dlength);
                    System.out.println("My Audio: " + cnt);
                    decryptedData = new byte[Dlength];
                    try {
                        decryptedData = rcvcaes2.getCipher().doFinal(tempBuffer);
                    } catch(GeneralSecurityException gse) {}

                    sourceDataLine.write(decryptedData, 0, decryptedData.length);
                }
            }
            sourceDataLine.drain();
            sourceDataLine.stop();
            sourceDataLine.close();

        } catch (Exception e) { e.printStackTrace(); }
    }

    public AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

}
