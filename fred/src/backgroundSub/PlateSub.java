package backgroundSub;

import com.atul.JavaOpenCV.Imshow;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.List;

/**
 * Created by jinhong on 09/03/2017.
 */
public class PlateSub implements BackGroundSub {

    private Boolean DEBUG;
    private Mat originalImage,filter_image;
    private ColourContain cp;
    private List<String> colour_plate;
    public Mat currentGray,backgroundGray,frameDelta,thresh;
    public List<MatOfPoint> cnts;
    public List<Rect> position_plate;
    public Size size = new Size(1,1);

    public PlateSub(Boolean DEBUG,Mat originalImage)
    {
        this.DEBUG = DEBUG;
        this.originalImage = originalImage;
    }

    @Override
    public List<String> image_processing(Mat frame) {


        Imgproc.cvtColor(frame, currentGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(currentGray, currentGray, size, 0);

        Imgproc.cvtColor(this.originalImage, backgroundGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(backgroundGray, backgroundGray, size, 0);

        if (this.DEBUG) {
            Imshow imshow1 = new Imshow("CurrentGaussianBlur");
            Imshow imshow2 = new Imshow("BackGroundGaussianBlur");
            imshow1.showImage(currentGray);
            imshow2.showImage(backgroundGray);
        }

        Imgproc.accumulateWeighted(currentGray, backgroundGray, 0.5);
        Core.convertScaleAbs(backgroundGray, backgroundGray);
        Core.absdiff(backgroundGray, currentGray, frameDelta);

        if (this.DEBUG) {
            Imshow imshow3 = new Imshow("frameDelta");
            imshow3.showImage(frameDelta);
        }

        Imgproc.threshold(frameDelta, thresh, 50, 255, Imgproc.THRESH_BINARY);

        if (this.DEBUG) {
            Imshow imshow4 = new Imshow("thresh1");
            imshow4.showImage(thresh);
        }

        Imgproc.dilate(thresh, thresh, new Mat(), new Point(-1, -1), 1);

        if (this.DEBUG) {
            Imshow imshow5 = new Imshow("thresh2");
            imshow5.showImage(thresh);
        }

        for(int i = 0; i < thresh.cols(); i++)
        {
            for(int j = 0; j < thresh.rows(); j++)
            {
                if(thresh.get(j,i)[0] == 255)
                {
                    filter_image.put(j,i,thresh.get(j,i));
                }
            }
        }

        colour_plate = cp.colour_contain(filter_image);


        return colour_plate;
    }
}
