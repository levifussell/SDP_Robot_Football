package backgroundSub;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import vision.tools.DirectedPoint;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinhong on 12/03/2017.
 */
public class JinVision {
    private Mat origin_Field;
    private Mat plate_Background = Imgcodecs.imread(getClass().getResource("img/plate.png").getPath());
    private FieldSub fieldSub;
    private PlateSub plateSub = new PlateSub(false,plate_Background);
    private List<String> detected_robot;

    public JinVision(){}

    public JinVision(Mat origin_Field)
    {
        this.origin_Field = origin_Field;
        this.fieldSub = new FieldSub(false,this.origin_Field);
    }

    public void image_Processing(Mat frame)
    {
        List<Rect> plate_position = fieldSub.image_processing(frame);
        List<DirectedPoint> locations = new ArrayList<DirectedPoint>();
        List<List<String>> colour_plates = new ArrayList<List<String>>();
        for(int i = 0; i < plate_position.size(); i++)
        {
            double x = (plate_position.get(i).br().x + plate_position.get(i).tl().x) / 2 ;
            double y = (plate_position.get(i).br().y + plate_position.get(i).tl().y) / 2 ;
            DirectedPoint location = new DirectedPoint(x,y,0);
            Mat plate_image = new Mat(frame,plate_position.get(i));
            detected_robot = plateSub.image_processing(plate_image);
            if(!detected_robot.isEmpty())
            {
                locations.add(location);
                colour_plates.add(detected_robot);
            }
        }
        RobotAnalysis.robot_Analysis.robotAnalysis(locations,colour_plates);
        System.out.println(RobotAnalysis.robot_Analysis.robots);
    }
}
