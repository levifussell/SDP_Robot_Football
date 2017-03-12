package backgroundSub;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jinhong on 09/03/2017.
 */
public class ColourContain {
    private List<String> colour_plate;
    private Mat hsv,mask,thresh,nonZeroCoordinates;
    private List<MatOfPoint> cnts;
    private List<Rect> position_circle;
    private HashMap<String,ArrayList<Scalar>> boundaries;
    private Scalar scalar = new Scalar(0,255,0);

    public ColourContain(){}

    public List<String> colour_contain(Mat image)
    {
        for(int i = 0; i < boundaries.size(); i++)
        {
            Imgproc.cvtColor(image,hsv,Imgproc.COLOR_BGR2HSV);
            Core.inRange(hsv,boundaries.get("").get(0),boundaries.get("").get(1),mask);

            Imgproc.erode(mask,thresh,new Mat(),new Point(-1,-1),1);

            Imgproc.findContours(thresh,cnts,new Mat(),Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
            Core.findNonZero(mask,nonZeroCoordinates);
            if(nonZeroCoordinates.total()>40 && cnts.size() > 0)
            {
                if(cnts.size() > 1)
                {
                    colour_plate.add(boundaries.keySet() + "3");
                }
                else
                {
                    colour_plate.add("");
                    Rect rect = Imgproc.boundingRect(cnts.get(0));
                    position_circle.add(rect);
                }
            }
        }

        if(position_circle.size() == 2)
        {

        }
        else
        {
            if(position_circle.size() == 1)
            {
                if(colour_plate.contains("blue") || colour_plate.contains("yellow"))
                {

                }
                else
                {
                    if(!colour_plate.contains("blue") && !colour_plate.contains("yellow"))
                    {
                        int x = (int)image.size().width / 2;
                        int y = (int)image.size().height / 2;
                        Imgproc.line(image,position_circle.get(0).tl(),new Point(x,y),scalar,2);
                    }
                }
            }

        }

        return colour_plate;
    }

}
