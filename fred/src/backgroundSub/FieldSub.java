package backgroundSub;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;
import com.atul.JavaOpenCV.Imshow;
import org.opencv.core.Point;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.util.List;

/**
 * Created by jinhong on 09/03/2017.
 */
public class FieldSub implements BackGroundSub {

    private Boolean DEBUG;
    private Mat originalImage;
    public Mat currentGray,backgroundGray,frameDelta,thresh;
    public List<MatOfPoint> cnts;
    public List<Rect> position_plate;
    public Size size = new Size(11,11);
    public Scalar scalar = new Scalar(0,255,0);

    public FieldSub(Boolean DEBUG,Mat originalImage)
    {
        this.DEBUG = DEBUG;
        this.originalImage = originalImage;
    }

    @Override
    public List<Rect> image_processing(Mat frame) {
         Imgproc.cvtColor(frame,currentGray,Imgproc.COLOR_BGR2GRAY);
         Imgproc.GaussianBlur(currentGray,currentGray,size,0);

         Imgproc.cvtColor(this.originalImage,backgroundGray,Imgproc.COLOR_BGR2GRAY);
         Imgproc.GaussianBlur(backgroundGray,backgroundGray,size,0);

         if(this.DEBUG)
         {
             Imshow imshow1 = new Imshow("CurrentGaussianBlur");
             Imshow imshow2 = new Imshow("BackGroundGaussianBlur");
             imshow1.showImage(currentGray);
             imshow2.showImage(backgroundGray);
         }

         Imgproc.accumulateWeighted(currentGray,backgroundGray,0.5);
         Core.convertScaleAbs(backgroundGray,backgroundGray);
         Core.absdiff(backgroundGray,currentGray,frameDelta);

         if(this.DEBUG)
         {
             Imshow imshow3 = new Imshow("frameDelta");
             imshow3.showImage(frameDelta);
         }

         Imgproc.threshold(frameDelta,thresh,30,255,Imgproc.THRESH_BINARY);

         if(this.DEBUG)
         {
             Imshow imshow4 = new Imshow("thresh1");
             imshow4.showImage(thresh);
         }

         Imgproc.dilate(thresh,thresh,new Mat(),new Point(-1,-1),4);

        if(this.DEBUG)
        {
            Imshow imshow5 = new Imshow("thresh2");
            imshow5.showImage(thresh);
        }

        Imgproc.findContours(thresh,cnts,new Mat(),Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);

        while(FieldSub.areaDetection(cnts))
        {
            Imgproc.erode(thresh,thresh,new Mat(),new Point(-1,-1),1);
            Imgproc.findContours(thresh,cnts,new Mat(),Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
        }

        for(int i = 0; i < cnts.size(); i ++)
        {
            Rect rect = Imgproc.boundingRect(cnts.get(i));
            Imgproc.rectangle(frame,rect.tl(),rect.br(),scalar,2);
            position_plate.add(rect);
        }

        return position_plate;

    }

    private static Boolean areaDetection(List<MatOfPoint> cnts)
    {
        for(int i = 0; i < cnts.size();i++)
        {
            if(Imgproc.contourArea(cnts.get(i)) > 1500)
            {
                return true;
            }
        }
        return false;
    }
}
