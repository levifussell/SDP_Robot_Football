import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveDouble;

import java.util.Arrays;

/**
 *  * Created by Siim on 10.02.2017.
 *   */
public class Test {
    public static void main(String[] args) {
        OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
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
	
	//MANDATORY EXCEPTION CATCH BECAUSE WTF
	try
	{
		octave.eval("");
	}
	catch(Exception e){System.out.println("CAUGHT"); }
	//now we start actually running the program
	octave.eval("cd ..");
	//octave.eval("main");
	octave.eval("I1 = imread('imgs/snap-unknown-20170206-180525-1.jpeg');");
	octave.eval("[roboPos, roboAngle, roboColour] = runVision(I1, 1600, 400000, 400000, 300000, 400000, 400000);");
	OctaveDouble rPos = octave.get(OctaveDouble.class, "roboPos");
	OctaveDouble rAngle = octave.get(OctaveDouble.class, "roboAngle");
	OctaveDouble rColour = octave.get(OctaveDouble.class, "roboColour");
	octave.close();
	System.out.println("RoboPos: " + Arrays.toString(rPos.getData()));
	System.out.println("RoboAngle: " + Arrays.toString(rAngle.getData()));
	System.out.println("RoboColour: " + Arrays.toString(rColour.getData()));
    }
}

