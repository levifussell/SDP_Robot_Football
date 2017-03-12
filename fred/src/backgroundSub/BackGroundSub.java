package backgroundSub;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import java.util.List;
import java.util.Objects;

/**
 * Created by jinhong on 09/03/2017.
 */
public interface BackGroundSub<T> {
    public List<T> image_processing(Mat image);
}
