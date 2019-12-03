package Streaming;

import ClientLogic.MultiClient;
import Crypto.CliAES;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import static org.opencv.highgui.HighGui.toBufferedImage;
import static org.opencv.videoio.Videoio.CAP_DSHOW;

public class CrypWebCam implements Runnable{
    static{System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    private MultiClient mc;
    private CliAES caes2;

    public CrypWebCam(MultiClient mc, String key) {
        System.out.println(mc.getId() + " WebCam Streaming Start");
        this.mc = mc;
        caes2 = new CliAES();
        try {
            caes2.createKey(key);
            caes2.modeEncrypt();
        } catch (UnsupportedEncodingException | GeneralSecurityException ue) { ue.printStackTrace(); }
    }

    @Override
    public void run() {
        VideoCapture cap = new VideoCapture(0, CAP_DSHOW);

        if(!cap.isOpened()) {
            System.exit(-1);
        }

        //Matrix for storing image
        Mat image = new Mat();

        try {
            while(!mc.getIsStop()) {
                //Read current camera frame into matrix
                cap.read(image);
                if(!image.empty()) {
                    sendImage(image);
                }
                else {
                    System.out.println("No captured frame -- camera disconnected");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        cap.release();
    }

    public void sendImage(Mat image) {
        BufferedImage bimg = toBufferI(toBufferedImage(image));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bimg, "jpg", baos);
            byte[] imageInByte = baos.toByteArray();
            mc.getStreamos().writeUTF("Send");
            byte[] encryptImageInByte = null;
            try {
                encryptImageInByte = caes2.getCipher().doFinal(imageInByte);
            } catch(GeneralSecurityException gse) { gse.printStackTrace(); }
            mc.getStreamos().writeInt(encryptImageInByte.length);
            mc.getStreamos().write(encryptImageInByte);
            baos.close();
        } catch(IOException ioe) { ioe.printStackTrace(); }
    }

    public BufferedImage toBufferI(Image img) {
        if(img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }


}
