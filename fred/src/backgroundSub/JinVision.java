package backgroundSub;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.List;

/**
 * Created by jinhong on 12/03/2017.
 */
public class JinVision {
    private Mat origin_Field;
    private Mat plate_Background = Imgcodecs.imread(getClass().getResource("img/plate.png").getPath());
    public Mat field_background = Imgcodecs.imread(getClass().getResource("img/0_0.jpeg").getPath());
    public Mat field = Imgcodecs.imread(getClass().getResource("img/4_0.jpeg").getPath());
    private FieldSub fieldSub;
    private PlateSub plateSub = new PlateSub(true,plate_Background);
    private int counter = 0;
    private List<String> detected_robot;

    public JinVision()
    {
        this.origin_Field = field_background;
    }

    public JinVision(Mat origin_Field)
    {
        this.origin_Field = origin_Field;
    }

    public void image_Processing(Mat frame)
    {
        fieldSub = new FieldSub(true,origin_Field);
        List<Rect> plate_position = fieldSub.image_processing(field);
        for(int i = 0; i < plate_position.size(); i++)
        {
            Mat plate_image = new Mat(field,plate_position.get(i));
            detected_robot = plateSub.image_processing(plate_image);
            System.out.println(detected_robot);
        }
    }

}
