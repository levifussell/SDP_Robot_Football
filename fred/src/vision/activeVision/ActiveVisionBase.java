package vision.activeVision;

import imageprocessing.java.CurrentVision;
import vision.rawInput.RawInputListener;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by s1408726 on 26/02/17.
 */
public class ActiveVisionBase implements RawInputListener {
    boolean processing = false;

    @Override
    public void nextFrame(BufferedImage image, long time) {
        preprocessImage(image, time);

		/*if (!processing) {
			new Thread(new Runnable() {

				public void run() {
					preprocessImage(image, time);
				}
			}).start();
		}*/
    }

    void preprocessImage(BufferedImage image, long time) {
        processing = true;
        //		try {
        // retrieve image
//    	File outputfile = new File("src/imageprocessing/imgs/saved.png");
//    	ImageIO.write(image, "png", outputfile);
//		} catch (IOException e) {
//			System.out.println("Sorry, i cant save the image because of levi");
//		}
//		CurrentVision.startImageProcess();

        long timeStart = System.currentTimeMillis();

        int width = 640;
        int height = 480;
        System.out.println(image.getWidth() + ", " + image.getHeight());
        double[] img = new double[height * width * 3];

        for(int x = 0; x < width; ++x)
        {
            for(int y = 0; y < height; ++y)
            {
                Color c = new Color(image.getRGB(x, y));
                img[x * height + y] = c.getRed();
                img[width*height + (x * height + y)] = c.getGreen();
                img[width*height*2 + (x * height + y)] = c.getBlue();
            }
        }

        System.out.println("Image parsing took: " + (System.currentTimeMillis() - timeStart) + " ms");
        timeStart = System.currentTimeMillis();

        CurrentVision.startImageProcess(img, image.getWidth(), image.getHeight());
        System.out.println("Octave processing took: " + (System.currentTimeMillis() - timeStart) + " ms");
        processing = false;
    }
}
