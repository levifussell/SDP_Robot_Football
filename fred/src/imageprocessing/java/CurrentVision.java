package imageprocessing.java;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveDouble;
import vision.gui.Preview;

import java.awt.*;
import java.util.Arrays;

/**
 *  * Created by Siim on 10.02.2017.
 *   */
public class CurrentVision {

	private static boolean isInitialised = false;
	private static final int MAX_FRAMES = 20;
	private static int countFirstFrames = MAX_FRAMES;
	private static boolean startTemplateDetect = false;
	private static boolean startBackgroundGrab = false;
	private static double[] template;
	private static double[] robotPos = {0.0, 0.0};
	private static double[][] backgroundSubtract = null;
	private static OctaveEngine octave;

	public static void launchOctaveProcess()
	{
		System.out.println("NEW octave state created");
		octave = new OctaveEngineFactory().getScriptEngine();
		isInitialised = true;

		//MANDATORY EXCEPTION CATCH BECAUSE WTF
		try
		{
			octave.eval("");
		}
		catch(Exception e){System.out.println("test3");System.out.println("CAUGHT"); }
		//now we start actually running the program
		octave.eval("cd src/tracking/");
	}
	public static void endOctaveProcess()
	{
		octave.close();
	}

	public static boolean shouldAssembleTemplate() { return countFirstFrames > 0 && startTemplateDetect; }
	public static boolean shouldAssembleBackground() { return countFirstFrames > 0 && startBackgroundGrab; }

	public static void selectRobotState(int posX, int posY)
	{
		if(startTemplateDetect) { return; }

		robotPos[0] = (int)(posY / 4.0);
		robotPos[1] = (int)(posX / 4.0);

		if(!startBackgroundGrab && backgroundSubtract == null)
			startBackgroundGrab = true;
		else
			startTemplateDetect = true;
	}

	public static void assembleBackground(double[] image)
	{
		if(shouldAssembleBackground()) {
			System.out.println("gettting background");
			if(countFirstFrames == MAX_FRAMES)
			{
				backgroundSubtract = new double[MAX_FRAMES][image.length];
			}

			backgroundSubtract[countFirstFrames - 1] = image;

			countFirstFrames--;

			if(countFirstFrames == 0)
			{
				double[] background = new double[image.length];

				for(int i = 0; i < image.length; ++i)
				{
					double[] pixelVals = new double[MAX_FRAMES];
					for(int j = 0; j < MAX_FRAMES; ++j)
					{
						pixelVals[j] = backgroundSubtract[j][i];
					}

					//get median value
					Arrays.sort(pixelVals);

					int middle = pixelVals.length/2;
					if (pixelVals.length%2 == 1) {
						background[i] = pixelVals[middle];
					} else {
						background[i] = (pixelVals[middle-1] + pixelVals[middle]) / 2.0;
					}
				}

				OctaveDouble img = new OctaveDouble(background, 120, 160, 3);

				octave.put("background", img);
				octave.eval("figure(900)");
				octave.eval("imagesc(uint8(background))");

				countFirstFrames = MAX_FRAMES;
				startBackgroundGrab = false;
			}
		}
	}

	public static void assembleTemplate(double[] image)
	{

		if(shouldAssembleTemplate())
		{
			if(countFirstFrames == MAX_FRAMES)
			{
				template = new double[image.length];
			}

			for(int i = 0; i < image.length; ++i)
			{
				template[i] += image[i];

				if(countFirstFrames == 1)
				{
					template[i] /= MAX_FRAMES;
				}
			}

			countFirstFrames--;

			if(countFirstFrames == 0)
			{
				OctaveDouble img = new OctaveDouble(template, 120, 160, 3);
				OctaveDouble pos = new OctaveDouble(robotPos, 1, 2);
				System.out.println(template.length);

				octave.put("avgImg", img);
				octave.put("roboPos", pos);

				//background subtraction
				octave.eval("figure(1214124)");
				octave.eval("imagesc(uint8(avgImg - background))");

//				background threshold subtraction
				octave.eval("I1_back = ((avgImg - background) > 50) .* avgImg");
				octave.eval("figure(121324)");
				octave.eval("imagesc(I1_back)");

				octave.eval("[templates] = buildTemplates(uint8(avgImg), roboPos, 15);");
//				octave.eval("templates");
			}
		}
	}

    public static void startImageProcess(double[] image, int width, int height) {
		long timeStart = System.currentTimeMillis();

		if(!isInitialised) {
			launchOctaveProcess();
			System.out.println("Initializing octave took: " + (System.currentTimeMillis() - timeStart) + " ms");
			timeStart = System.currentTimeMillis();
		}

		if(shouldAssembleBackground())
		{
			assembleBackground(image);
		}
		else if(shouldAssembleTemplate())
		{
			assembleTemplate(image);
		}
		else if(startTemplateDetect) {

			OctaveDouble img = new OctaveDouble(image, 120, 160, 3);

			System.out.println("Creating octave matrix from image took: " + (System.currentTimeMillis() - timeStart) + " ms");
			timeStart = System.currentTimeMillis();

			octave.put("I1", img);
			octave.eval("I1 = uint8(I1 - background);");

			System.out.println("Inserting image to octave took: " + (System.currentTimeMillis() - timeStart) + " ms");
			timeStart = System.currentTimeMillis();

			//background subtraction
			octave.eval("figure(1214124)");
			octave.eval("imagesc(I1)");

//			octave.eval("size(templates)");
			octave.eval("[roboPos, roboAngle] = runTracker(uint8(I1), uint8(roboPos), templates, 100);");

			System.out.println("Running vision took: " + (System.currentTimeMillis() - timeStart) + " ms");
			timeStart = System.currentTimeMillis();

//				background threshold subtraction
//			octave.eval("thresh_r = 35;");
//			octave.eval("thresh_g = 60;");
//			octave.eval("thresh_b = 50;");
//			octave.eval("I1_back = ((I1 - background) > 50) .* I1");
			octave.eval("diffI = uint8(I1)");
			octave.eval("I1_back = ceil((((diffI(:, :, 1)) >= 35) + ((diffI(:, :, 2)) >= 60) + ((diffI(:, :, 3)) >= 50)) / 3.0)");
			octave.eval("figure(121324)");
			octave.eval("imagesc(I1_back)");

//			octave.eval("roboPos");
//			octave.eval("roboAngle");

			System.out.println("Evaluating roboPos took: " + (System.currentTimeMillis() - timeStart) + " ms");
			timeStart = System.currentTimeMillis();

			OctaveDouble rPos = octave.get(OctaveDouble.class, "roboPos");
			OctaveDouble rAngle = octave.get(OctaveDouble.class, "roboAngle");

			System.out.println("Getting results from octave took: " + (System.currentTimeMillis() - timeStart) + " ms");
			timeStart = System.currentTimeMillis();

			double[] rPosData = rPos.getData();

			if (Preview.preview.drawnImage != null) {
				Color c2 = new Color(0, 255, 0);

				for (int i = 0; i < rPosData.length / 2; i++) {
					for (int x = 0; x < 5; ++x) {
						for (int y = 0; y < 5; ++y) {
							int posY1 = Math.min(Preview.preview.drawnImage.getHeight() - 1, Math.max(0, (int) rPosData[i] * 4 + y));
							int posX1 = Math.min(Preview.preview.drawnImage.getWidth() - 1, Math.max(0, (int) rPosData[rPosData.length / 2 + i] * 4 + x));

							Preview.preview.drawnImage.setRGB(posX1, posY1, c2.getRGB());
						}
					}
				}
			}
			System.out.println("Drawing preview took: " + (System.currentTimeMillis() - timeStart) + " ms");
			timeStart = System.currentTimeMillis();
		}

//	octave.close();
//	System.out.println("RoboPos: " + Arrays.toString(rPos.getData()));
//	System.out.println("RoboAngle: " + Arrays.toString(rAngle.getData()));
//	System.out.println("RoboColour: " + Arrays.toString(rColour.getData()));
//		System.out.println("aPatches: " + Arrays.toString(aPatches.getData()));
    }
}

