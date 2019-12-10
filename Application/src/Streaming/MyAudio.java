package Streaming;

import ClientLogic.MultiClient;

import javax.sound.sampled.*;

public class MyAudio implements Runnable {
    //Member
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;  //microphone

    private MultiClient mc;

    //Constructor
    public MyAudio(MultiClient mc) {
        System.out.println(mc.getId() + " Audio Streaming Start");
        this.mc = mc;
    }

    //Method
    @Override
    public void run() {
        try {
            Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
            System.out.println(mixerInfo.length);
            for(int cnt = 0; cnt < mixerInfo.length; cnt++) {
                System.out.println(mixerInfo[cnt].getName());
            }
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            Mixer mixer = AudioSystem.getMixer(mixerInfo[2]);
            targetDataLine = (TargetDataLine)mixer.getLine(dataLineInfo);
            //targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            byte[] tempBuffer = new byte[10000];

            while (!mc.getIsStop()) {
                int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                mc.getAudioos().write(tempBuffer);
            }
            mc.getAudioos().write(0);
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
