package backgroundSub;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;

/**
 * Created by jinhong on 13/03/2017.
 */
public class Test {
    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat frame = Imgcodecs.imread("img/4_0.jpeg",Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);

        JinVision jinVision = new JinVision();
        jinVision.image_Processing(frame);
    }
}
