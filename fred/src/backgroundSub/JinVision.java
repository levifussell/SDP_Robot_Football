package backgroundSub;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Created by jinhong on 12/03/2017.
 */
public class JinVision {
    private Mat origin_Field;
    private Mat plate_Background = Imgcodecs.imread(getClass().getResource("img/plate.png").getPath());
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
    public static Mat bufferImg2Mat(BufferedImage in)
    {
        Mat out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC3);
        System.out.print("test1");
        byte[] data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
        System.out.print("test2");
        int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
        System.out.print("test3");
        for (int i = 0; i < dataBuff.length; i++) {
            data[i * 3] = (byte) ((dataBuff[i]));
            data[i * 3 + 1] = (byte) ((dataBuff[i]));
            data[i * 3 + 2] = (byte) ((dataBuff[i]));
        }
        out.put(0, 0, data);
        return out;
    }
}
