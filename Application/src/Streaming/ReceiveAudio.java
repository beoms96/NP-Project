package Streaming;

import ClientLogic.MultiClient;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class ReceiveAudio implements Runnable{
    //Member
    private MultiClient mc;
    private AudioFormat audioFormat;
    private SourceDataLine sourceDataLine;

    //Constructor
    public ReceiveAudio(MultiClient mc) {
        this.mc = mc;
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

            byte[] tempBuffer = new byte[10000];

            while (!mc.getIsStop()) {
                int cnt = mc.getAudiois().read(tempBuffer);
                System.out.println("My Audio: " + cnt + " Buffer: " + tempBuffer);
                if(tempBuffer.equals(0))
                    break;
                sourceDataLine.write(tempBuffer, 0, 10000);
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
