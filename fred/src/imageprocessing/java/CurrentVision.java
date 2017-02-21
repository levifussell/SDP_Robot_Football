package imageprocessing.java;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveDouble;
import vision.gui.Preview;

import java.awt.*;
import java.util.Arrays;
import java.awt.image.BufferedImage;

/**
 *  * Created by Siim on 10.02.2017.
 *   */
public class CurrentVision {

	private static boolean isInitialised = false;
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
		octave.eval("cd src/imageprocessing/");
	}
	public static void endOctaveProcess()
	{
		octave.close();
	}

    public static void startImageProcess() {

		if(!isInitialised)
			launchOctaveProcess();

		System.out.println("test1");
//        OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        OctaveDouble a = new OctaveDouble(new double[]{1, 2, 3, 4}, 2, 2);
        //octave.put("a", a);
	//octave.eval("a");

	//OctaveDouble varX = (OctaveDouble)octave.get("a");
	//System.out.println("RES: " + varX);
	
        //String func = "" //
        //        + "function res = my_func(a)\n" //
        //        + " res = 2 * a;\n" //
        //        + "endfunction\n" //
        //        + "";
        //octave.eval(func);
        //octave.eval("b = my_func(a);");
        //OctaveDouble b = octave.get(OctaveDouble.class, "b");
	//octave.close();
	//System.out.println(Arrays.toString(b.getData()));

	//octave.eval("main");
	octave.eval("I1 = imread('imgs/saved.png');");
	octave.eval("[roboPos, roboAngle, roboColour, activePatches] = runVision(I1, 1600, 1000000, 10000000, 1000000, 10000000, 10000000);");
		octave.eval("roboPos");
	OctaveDouble rPos = octave.get(OctaveDouble.class, "roboPos");
	OctaveDouble rAngle = octave.get(OctaveDouble.class, "roboAngle");
	OctaveDouble rColour = octave.get(OctaveDouble.class, "roboColour");
		OctaveDouble aPatches = octave.get(OctaveDouble.class, "activePatches");
	double[] aPatchesData = aPatches.getData();
		double[] rPosData = rPos.getData();

		if(Preview.preview.originalImage != null) {
			Color c = new Color(255, 0, 0);
			Color c2 = new Color(0, 255, 0);
			for(int i = 0; i < aPatchesData.length; i += 4)
			{
				int posY1 = Math.min(Preview.preview.drawnImage.getHeight(), Math.max(0, (int)aPatchesData[i + 3]));
				int posX1 = Math.min(Preview.preview.drawnImage.getWidth(), Math.max(0, (int)aPatchesData[i + 2]));
				int posY2 = Math.min(Preview.preview.drawnImage.getHeight(), Math.max(0, (int)aPatchesData[i]));
				int posX2 = Math.min(Preview.preview.drawnImage.getWidth(), Math.max(0, (int)aPatchesData[i + 1]));
				for(int x = posX1; x < posX2; ++x)
				{
					for(int y = posY1; y < posY2; ++y)
					{
						if((x == posX1 || x == posX2 - 1) || (y == posY1 || y == posY2 - 1))
						Preview.preview.drawnImage.setRGB(x, y, c.getRGB());
					}
				}
			}
//			for(int i = 0; i < 5; ++i)
//			{
//				for(int j = 0; j < 5; ++j)
//				{
//					Preview.preview.drawnImage.setRGB(i, j, c.getRGB());
//				}
//			}
			for(int i = 0; i < rPosData.length / 2; i++)
			{
				for(int x = 0; x < 5; ++x) {
					for(int y = 0; y < 5; ++y) {
						int posY1 = Math.min(Preview.preview.drawnImage.getWidth(), Math.max(0, (int) rPosData[i] + y));
						int posX1 = Math.min(Preview.preview.drawnImage.getWidth(), Math.max(0, (int) rPosData[rPosData.length / 2 + i] + x));

						Preview.preview.drawnImage.setRGB(posX1, posY1, c2.getRGB());
					}
				}
			}
		}

//	octave.close();
//	System.out.println("RoboPos: " + Arrays.toString(rPos.getData()));
//	System.out.println("RoboAngle: " + Arrays.toString(rAngle.getData()));
//	System.out.println("RoboColour: " + Arrays.toString(rColour.getData()));
//		System.out.println("aPatches: " + Arrays.toString(aPatches.getData()));
    }
}

