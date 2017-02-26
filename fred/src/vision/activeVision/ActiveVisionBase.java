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

        int width = 160;
        int height = 120;
        System.out.println(image.getWidth() + ", " + image.getHeight());
        double[] img = new double[height * width];

        for(int x = 0; x < width; x += 4)
        {
            for(int y = 0; y < height; y += 4)
            {
                Color c = new Color(image.getRGB(x, y));
//                img[x * height + y] = c.getRed();
//                img[width*height + (x * height + y)] = c.getGreen();
//                img[width*height*2 + (x * height + y)] = c.getBlue();
                double mean = (c.getRed() + c.getGreen() + c.getBlue()) / 3.0;
                double var = Math.sqrt(Math.pow(c.getRed() - mean, 2) +
                        Math.pow(c.getGreen() - mean, 2) +
                        Math.pow(c.getBlue() - mean, 2));
                img[x * height + y] = var;
            }
        }

        System.out.println("Image parsing took: " + (System.currentTimeMillis() - timeStart) + " ms");
        timeStart = System.currentTimeMillis();

        CurrentVision.startImageProcess(img, image.getWidth(), image.getHeight());
        System.out.println("Octave processing took: " + (System.currentTimeMillis() - timeStart) + " ms");
        processing = false;
    }
}
