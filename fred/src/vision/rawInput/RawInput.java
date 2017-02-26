package vision.rawInput;


import imageprocessing.java.CurrentVision;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
/**
 * Created by Simon Rovder
 */
public class RawInput extends JPanel{
	
	private JTabbedPane tabbedPane;
	
	public BufferedImage lastImage;
	
	private AbstractRawInput[] rawInputs = {
		LiveCameraInput.liveCameraInput,
		StaticImage.staticImage
	};
	
	private LinkedList<RawInputListener> imageListeners;
	
	public static final RawInput rawInputMultiplexer = new RawInput(); 
	
	
	private RawInput(){
		super();
		this.setLayout(new BorderLayout(0, 0));
		
		this.tabbedPane     = new JTabbedPane(JTabbedPane.TOP);
		this.imageListeners = new LinkedList<RawInputListener>();
		
		this.add(this.tabbedPane);
		
		for(AbstractRawInput rawInput : this.rawInputs){
			rawInput.setInputListener(this);
			this.tabbedPane.addTab(rawInput.getTabName(), null, rawInput, null);
		}
	}
	
	public static void addRawInputListener(RawInputListener ril){
		RawInput.rawInputMultiplexer.imageListeners.add(ril);
	}
	
	public void nextFrame(BufferedImage image, long time){
		this.lastImage = image;
//		try {
    	// retrieve image
//    	File outputfile = new File("src/imageprocessing/imgs/saved.png");
//    	ImageIO.write(image, "png", outputfile);
//		} catch (IOException e) {
//			System.out.println("Sorry, i cant save the image because of levi");
//		}
//		CurrentVision.startImageProcess();
//		for(RawInputListener ril : this.imageListeners){
//			ril.nextFrame(image, time);
//		}
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

		CurrentVision.startImageProcess(img, image.getWidth(), image.getHeight());

	}

	public void stopAllInputs(){
		for(RawInputInterface input : this.rawInputs){
			input.stop();
		}

		CurrentVision.endOctaveProcess();
	}

	public void setVideoChannel(int port){
		((LiveCameraInput)(this.rawInputs[0])).setVideoChannel(port);
	}
	
	public void streamVideo(){
		this.rawInputs[0].start();
	}
	
	
}
