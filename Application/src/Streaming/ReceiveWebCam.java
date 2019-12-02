package Streaming;

import ClientLogic.MultiClient;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import static org.opencv.highgui.HighGui.toBufferedImage;


public class ReceiveWebCam implements Runnable{
    static{System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    private MultiClient mc;
    private final JFrame frame;
    private final MyPanel panel;

    public ReceiveWebCam(MultiClient mc) {
        System.out.println(mc.getId() + "Receiving Start");
        this.mc = mc;

        frame = new JFrame("Camera Streaming");
        frame.getContentPane().setLayout(new FlowLayout());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.setVisible(false);
                setStop(true);
                try {
                    mc.getStreamos().writeUTF(mc.getId() + "#Quit");
                } catch(IOException ioe) { ioe.printStackTrace(); }

            }
        });

        //JPanel is used for drawing image
        panel = new MyPanel();
        frame.getContentPane().add(panel);
    }

    @Override
    public void run() {
        //Matrix for storing image
        Mat image;
        frame.setVisible(true);

        try {
            while(!mc.getIsStop()) {
                //Read current camera frame into matrix
                image = receiveCam();
                if(!image.empty()) {
                    render(image);
                }
                else {
                    System.out.println("No captured frame -- camera disconnected");
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

    public void setStop(boolean stop) {
        mc.setIsStop(stop);
    }

    public Mat receiveCam() {
        Mat image = new Mat();
        try {
            int length = mc.getRcvstreamis().readInt();
            byte[] data = null;
            if(length>0) {
                data = new byte[length];
                mc.getRcvstreamis().readFully(data, 0, data.length);
            }
            else if(length == 0) {
                frame.setVisible(false);
                setStop(true);
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
