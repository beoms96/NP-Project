package Streaming;

import ClientLogic.MultiClient;
import Crypto.CliAES;

import javax.sound.sampled.*;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class CrypAudio implements Runnable{
    //Member
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;  //microphone

    private MultiClient mc;
    private CliAES caes3;

    //Constructor
    public CrypAudio(MultiClient mc, String key) {
        System.out.println(mc.getId() + " Audio Streaming Start");
        this.mc = mc;
        caes3 = new CliAES();
        try {
            caes3.createKey(key);
            caes3.modeEncrypt();
        } catch (UnsupportedEncodingException | GeneralSecurityException ue) { ue.printStackTrace(); }
    }

    //Method
    @Override
    public void run() {
        try {
            Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            Mixer mixer = AudioSystem.getMixer(mixerInfo[2]);
            targetDataLine = (TargetDataLine)mixer.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            byte[] tempBuffer = new byte[10000];
            byte[] encryptBuffer = null;

            while (!mc.getIsStop()) {
                int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                try {
                    encryptBuffer = caes3.getCipher().doFinal(tempBuffer);
                } catch(GeneralSecurityException gse) { gse.printStackTrace(); }
                mc.getAudioos().writeInt(encryptBuffer.length);
                mc.getAudioos().write(encryptBuffer);
                mc.getAudioos().flush();
            }
            mc.getAudioos().writeInt(0);
            System.out.println("Audio Terminate");
            targetDataLine.drain();
            targetDataLine.stop();
            targetDataLine.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
