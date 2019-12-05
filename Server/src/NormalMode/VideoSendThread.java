package NormalMode;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.opencv.highgui.HighGui.toBufferedImage;

public class VideoSendThread implements Runnable{
    //Member
    private String filename;
    private StreamServerThread sst;

    //Constructor
    public VideoSendThread(StreamServerThread sst, String filename) {
        this.filename = filename;
        this.sst = sst;
    }

    //Method
    @Override
    public void run() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture cap = new VideoCapture();

        System.out.println(filename);
        cap.open(filename);

        int video_length = (int) cap.get(Videoio.CAP_PROP_FRAME_COUNT);

        Mat frame = new Mat();

        if(cap.isOpened()) {
            System.out.println("Video is opened");
            System.out.println("Number of Frames: " + video_length);

            while(sst.getVideostart()) {
                if(!cap.read(frame)) { //last frame
                    try {
                        sst.getRcvdos().writeInt(0);
                    }catch(IOException ioe) { ioe.printStackTrace(); }
                    sst.setVideostart(false);
                    break;
                }
                else {
                    if(!frame.empty())
                        sendFrame(frame);
                    else {
                        try {
                            sst.getRcvdos().writeInt(0);
                        }catch(IOException ioe) { ioe.printStackTrace(); }
                        sst.setVideostart(false);
                        System.out.println("No captured frame -- frame nothing");
                    }
                }
            }
            cap.release();
            System.out.println(video_length + " Frames extracted");
        }
        else {
            try {
                sst.getRcvdos().writeInt(0);
            }catch(IOException ioe) { ioe.printStackTrace(); }
        }
    }

    public void sendFrame(Mat image) {
        BufferedImage bimg = toBufferI(toBufferedImage(image));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bimg, "jpg", baos);
            byte[] imageInByte = baos.toByteArray();
            sst.getRcvdos().writeInt(imageInByte.length);
            sst.getRcvdos().write(imageInByte);
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
