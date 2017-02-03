import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;

class MaxPooling
{

    public static void main(String[] args)
    {
        BufferedImage img = null;

        try
        {
            img = ImageIO.read(new File("imgs/snap-unknown-20170126-134652-1.jpeg"));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

}
