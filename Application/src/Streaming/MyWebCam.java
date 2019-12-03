package Streaming;

import ClientLogic.MultiClient;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.opencv.highgui.HighGui.toBufferedImage;
import static org.opencv.videoio.Videoio.CAP_DSHOW;

public class MyWebCam implements Runnable{
    static{System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    private MultiClient mc;

    public MyWebCam(MultiClient mc) {
        System.out.println(mc.getId() + " WebCam Streaming Start");
        this.mc = mc;
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
            mc.getStreamos().writeInt(imageInByte.length);
            mc.getStreamos().write(imageInByte);
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
