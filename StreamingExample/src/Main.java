import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class Main {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture cap = new VideoCapture();

        String path = System.getProperty("user.dir");
        String input = "V-IDOL.mp4";
        System.out.println(input);

        cap.open(input);

        int video_length = (int) cap.get(Videoio.CAP_PROP_FRAME_COUNT);
        int frames_per_second = (int) cap.get(Videoio.CAP_PROP_FPS);
        int frame_number = 0;

        Mat frame = new Mat();

        if(cap.isOpened())
        {
            System.out.println("Video is opened");
            System.out.println("Number of Frames: " + video_length);
            System.out.println(frames_per_second + " Frames per Second");
            System.out.println("Converting Video...");

            while(cap.read(frame)) {
                frame_number++;
            }

            System.out.println(frame_number);
            cap.release();

            System.out.println(video_length + " Frames extracted");
        }
        else {
            System.out.println("Fail");
        }
    }
}
