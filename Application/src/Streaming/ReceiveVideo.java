package Streaming;

import ClientLogic.MultiClient;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.opencv.highgui.HighGui.toBufferedImage;

public class ReceiveVideo implements Runnable{
    static{System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
    //Member
    private MultiClient mc;
    private final JFrame frame;
    private final MyPanel panel;

    //Constructor
    public ReceiveVideo(MultiClient mc, String filename) {
        System.out.println(mc.getId() + " Receiving Start");
        this.mc = mc;
        frame = new JFrame(filename + " Video Streaming");
        frame.getContentPane().setLayout(new FlowLayout());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.setVisible(false);
                try {
                    mc.getStreamos().writeUTF("ClientQuit");
                } catch(IOException ioe) { ioe.printStackTrace(); }
            }
        });

        //JPanel is used for drawing image
        panel = new MyPanel();
        frame.getContentPane().add(panel);
    }

    //Method
    @Override
    public void run() {
        Mat image;
        frame.setVisible(true);

        try {
            while(!mc.getIsStop()) {
                //Read current camera frame into matrix
                image = receiveVideo();
                if(image == null) {
                    frame.setVisible(false);
                    JOptionPane.showMessageDialog(mc.getJf(), "Terminate Streaming");
                }
                else {
                    if(!image.empty()) {
                        render(image);
                    }
                    else {
                        frame.setVisible(false);
                        System.out.println("No captured frame -- frame nothing");
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void render(Mat image) {
        Image i = toBufferedImage(image);
        panel.setImage(i);
        panel.repaint();
        frame.pack();
    }

    public Mat receiveVideo() {
        Mat image = new Mat();
        try {
            int length = mc.getRcvstreamis().readInt();
            System.out.println("data length: " + length);
            byte[] data = null;
            if(length>0) {
                data = new byte[length];
                mc.getRcvstreamis().readFully(data, 0, data.length);
            }
            else if(length == 0) {
                mc.setIsStop(true);
                return null;
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            BufferedImage imag = ImageIO.read(bais);
            image = bufferedImageToMat(imag);
            bais.close();
        } catch(IOException ioe) { ioe.printStackTrace(); }

        return image;
    }

    public Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0,0,data);
        return mat;
    }
}
