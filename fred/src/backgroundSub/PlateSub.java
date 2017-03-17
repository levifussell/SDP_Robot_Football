package backgroundSub;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinhong on 09/03/2017.
 */
public class PlateSub implements BackGroundSub {

    private Boolean DEBUG;
    private Mat originalImage = new Mat();
    private ColourContain cp = new ColourContain(true);
    public Size size = new Size(1,1);
    public Imshow imshow1 = new Imshow("CurrentGaussianBlur");
    public Imshow imshow2 = new Imshow("BackGroundGaussianBlur");
    public Imshow imshow3 = new Imshow("frameDelta");
    public Imshow imshow4 = new Imshow("thresh1");
    public Imshow imshow5 = new Imshow("thresh2");
    public Imshow imshow6 = new Imshow("filter image");

    public PlateSub(Boolean DEBUG,Mat originalImage)
    {
        this.DEBUG = DEBUG;
        Imgproc.resize(originalImage,this.originalImage,new Size(30,30));
    }

    @Override
    public List<String> image_processing(Mat frame) {
        Mat currentGray = new Mat(),backgroundGray = new Mat(),frameDelta = new Mat(),thresh = new Mat();
        List<String> colour_plate = new ArrayList<String>();

        Mat filter_image = new Mat(30, 30, CvType.CV_8UC3, new Scalar(0,0,0));

        Mat resized_frame = new Mat();
        Imgproc.resize(frame,resized_frame,new Size(30,30));


        Imgproc.cvtColor(resized_frame, currentGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(currentGray, currentGray, size, 0);

        Imgproc.cvtColor(this.originalImage, backgroundGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(backgroundGray, backgroundGray, size, 0);

        if (this.DEBUG) {
            imshow1.showImage(currentGray);
            imshow2.showImage(backgroundGray);
        }

//        Imgproc.accumulateWeighted(currentGray, backgroundGray, 0.5);
        Core.convertScaleAbs(backgroundGray, backgroundGray);
        Core.absdiff(backgroundGray, currentGray, frameDelta);

        if (this.DEBUG) {
            imshow3.showImage(frameDelta);
        }

        Imgproc.threshold(frameDelta, thresh, 50, 255, Imgproc.THRESH_BINARY);

        if (this.DEBUG) {
            imshow4.showImage(thresh);
        }

        Imgproc.dilate(thresh, thresh, new Mat(), new Point(-1, -1), 1);

        if (this.DEBUG) {
            imshow5.showImage(thresh);
        }

        for(int i = 0; i < thresh.cols(); i++)
        {
            for(int j = 0; j < thresh.rows(); j++)
            {
                if(thresh.get(j,i)[0] == 255)
                {
                    filter_image.put(j,i,resized_frame.get(j,i));
                }
            }
        }
        if(this.DEBUG)
        {
            imshow6.showImage(filter_image);
        }
        colour_plate = cp.colour_contain(filter_image);

        return colour_plate;
    }
}
