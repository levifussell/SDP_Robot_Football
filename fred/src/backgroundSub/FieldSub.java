package backgroundSub;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinhong on 09/03/2017.
 */
public class FieldSub implements BackGroundSub {

    private Boolean DEBUG;
    private Mat originalImage;
    public static List<MatOfPoint> cnts = new ArrayList<MatOfPoint>();
    public List<Rect> position_plate;
    public Size size = new Size(11,11);
    public Scalar scalar = new Scalar(0,255,0);
    public Imshow imshow1 = new Imshow("CurrentGaussianBlur");
    public Imshow imshow2 = new Imshow("BackGroundGaussianBlur");
    public Imshow imshow3 = new Imshow("frameDelta");
    public Imshow imshow4 = new Imshow("thresh1");
    public Imshow imshow5 = new Imshow("thresh2");
    public Imshow frame_show = new Imshow("frame");

    public FieldSub(Boolean DEBUG,Mat originalImage)
    {
        this.DEBUG = DEBUG;
        this.originalImage = originalImage;
    }

    @Override
    public List<Rect> image_processing(Mat frame) {
         Mat frame_copy = frame.clone();
         Mat currentGray = new Mat(),backgroundGray = new Mat(),frameDelta = new Mat(),thresh = new Mat();
         cnts = new ArrayList<MatOfPoint>();
         position_plate = new ArrayList<Rect>();
         Imgproc.cvtColor(frame,currentGray,Imgproc.COLOR_BGR2GRAY);
         Imgproc.GaussianBlur(currentGray,currentGray,size,0);

         Imgproc.cvtColor(this.originalImage,backgroundGray,Imgproc.COLOR_BGR2GRAY);
         Imgproc.GaussianBlur(backgroundGray,backgroundGray,size,0);
         if(this.DEBUG)
         {
             imshow1.showImage(currentGray);
             imshow2.showImage(backgroundGray);
         }

         //Imgproc.accumulateWeighted(currentGray,backgroundGray,0.5);
         Core.convertScaleAbs(backgroundGray,backgroundGray);
         Core.absdiff(backgroundGray,currentGray,frameDelta);

         if(this.DEBUG)
         {
             imshow3.showImage(frameDelta);
         }

         Imgproc.threshold(frameDelta,thresh,30,255,Imgproc.THRESH_BINARY);

         if(this.DEBUG)
         {
             imshow4.showImage(thresh);
         }

         Imgproc.dilate(thresh,thresh,new Mat(),new Point(-1,-1),4);

        if(this.DEBUG)
        {
            imshow5.showImage(thresh);
        }

        Imgproc.findContours(thresh,cnts,new Mat(),Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);

        while(FieldSub.areaDetection(cnts))
        {
            Imgproc.erode(thresh,thresh,new Mat(),new Point(-1,-1),1);
            cnts = new ArrayList<MatOfPoint>();
            Imgproc.findContours(thresh,cnts,new Mat(),Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
        }

        for(int i = 0; i < cnts.size(); i ++)
        {
            Rect rect = Imgproc.boundingRect(cnts.get(i));
            Imgproc.rectangle(frame_copy,rect.tl(),rect.br(),scalar,2);
            position_plate.add(rect);
        }
        frame_show.showImage(frame_copy);

        return position_plate;

    }

    private static Boolean areaDetection(List<MatOfPoint> cnts)
    {
        for(int i = 0; i < cnts.size();i++)
        {
            if(Imgproc.contourArea(cnts.get(i)) > 2200 && Imgproc.contourArea(cnts.get(i)) < 4000)
            {
                return true;
            }
            if(Imgproc.contourArea(cnts.get(i)) < 200)
            {
                cnts.remove(i);
                i--;
            }
        }
        return false;
    }
}
