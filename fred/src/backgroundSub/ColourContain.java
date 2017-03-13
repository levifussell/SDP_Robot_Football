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
    private List<String> colour_plate = new ArrayList<String>();
    private Mat hsv = new Mat(),mask = new Mat(),thresh = new Mat(),nonZeroCoordinates = new Mat();
    private List<MatOfPoint> cnts = new ArrayList<MatOfPoint>();
    private List<Rect> position_circle = new ArrayList<Rect>();
    private HashMap<String,ArrayList<Scalar>> boundaries = new HashMap<String,ArrayList<Scalar>>();
    private List<String> color = new ArrayList<String>();
    private Scalar scalar = new Scalar(0,255,0);
    private OpenCVGUI openCVGUI;
    private boolean Debug;

    public ColourContain(boolean Debug)
    {
        color.add("red");
        color.add("yellow");
        color.add("blue");
        color.add("green");
        this.Debug = Debug;
        openCVGUI = new OpenCVGUI();

    }

    public List<String> colour_contain(Mat image)
    {
        getColourRange();
        for(int i = 0; i < boundaries.size(); i++)
        {
            Imgproc.cvtColor(image,hsv,Imgproc.COLOR_BGR2HSV);
            Core.inRange(hsv,boundaries.get(color.get(i)).get(0),boundaries.get(color.get(i)).get(1),mask);

            Imgproc.erode(mask,thresh,new Mat(),new Point(-1,-1),1);

            if(this.Debug)
            {
                Imshow imshow = new Imshow("mask" + color.get(i));
                imshow.showImage(thresh);
            }

            Imgproc.findContours(thresh,cnts,new Mat(),Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
            Core.findNonZero(mask,nonZeroCoordinates);
            if(nonZeroCoordinates.total()>40 && cnts.size() > 0)
            {
                if(cnts.size() > 1)
                {
                    colour_plate.add(color.get(i) + "3");
                }
                else
                {
                    colour_plate.add(color.get(i));
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
    public void getColourRange()
    {
        ArrayList<Scalar> red = new ArrayList<Scalar>();
        red.add(openCVGUI.getRedLower());
        red.add(openCVGUI.getRedUpper());
        boundaries.put("red",red);

        ArrayList<Scalar> yellow = new ArrayList<Scalar>();
        yellow.add(openCVGUI.getYellowLower());
        yellow.add(openCVGUI.getYellowUpper());
        boundaries.put("yellow",yellow);

        ArrayList<Scalar> blue = new ArrayList<Scalar>();
        blue.add(openCVGUI.getBlueLower());
        blue.add(openCVGUI.getBlueUpper());
        boundaries.put("blue",blue);

        ArrayList<Scalar> green = new ArrayList<Scalar>();
        green.add(openCVGUI.getGreenLower());
        green.add(openCVGUI.getGreenUpper());
        boundaries.put("green",green);
    }

}
