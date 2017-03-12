package backgroundSub;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.List;

/**
 * Created by jinhong on 12/03/2017.
 */
public class JinVision {
    private Mat origin_Field;
    private Mat plate_Background = Imgcodecs.imread(getClass().getResource("backgroundSub/img/plate.png").getPath());
    private FieldSub fieldSub;
    private PlateSub plateSub = new PlateSub(false,plate_Background);
    private int counter = 0;
    private List<String> detected_robot;

    public JinVision(Mat origin_Field)
    {
        this.origin_Field = origin_Field;
    }

    public void image_Processing(Mat frame)
    {
        fieldSub = new FieldSub(false,origin_Field);
        List<Rect> plate_position = fieldSub.image_processing(frame);
        for(int i = 0; i < plate_position.size(); i++)
        {
            Mat plate_image = new Mat(frame,plate_position.get(i));
            detected_robot = plateSub.image_processing(plate_image);
        }
        System.out.print(detected_robot);
    }
}
