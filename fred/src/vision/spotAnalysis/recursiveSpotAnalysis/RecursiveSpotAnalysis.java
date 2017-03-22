package vision.spotAnalysis.recursiveSpotAnalysis;

import vision.*;
import vision.Robot;
import vision.colorAnalysis.SDPColor;
import vision.colorAnalysis.SDPColorInstance;
import vision.colorAnalysis.SDPColors;
import vision.constants.Constants;
import vision.gui.Preview;
import vision.multipleRegions.MultipleRegions;
import vision.spotAnalysis.SpotAnalysisBase;
import vision.spotAnalysis.approximatedSpotAnalysis.Spot;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static vision.tools.ImageTools.rgbToHsv;

/**
 * Created by Simon Rovder
 */
public class RecursiveSpotAnalysis extends SpotAnalysisBase{

    private int[] rgb;
    private float[] hsv;
    private SDPColor[] found;
    private Robot diag4;
    private double robot_x = 0,previ_robot_x = 1;
    private double robot_y = 0,previ_robot_y = 1;
    private SDPColorInstance colorInstance;
    private HashMap<String,HashMap<SDPColor,SDPColorInstance>> map = new HashMap<String,HashMap<SDPColor, SDPColorInstance>>();
    private boolean check = false;
    private boolean check_break = false;


    public RecursiveSpotAnalysis(){
        super();
        // Have arrays of 4 times the size for the inputs\
        // (for red, green, blue, alpha OR hue, saturation, value, alpha)
        this.rgb   = new int[4* Constants.INPUT_WIDTH*Constants.INPUT_HEIGHT];
        this.hsv   = new float[4*Constants.INPUT_WIDTH*Constants.INPUT_HEIGHT];

        // array to keep track of visited spots
        this.found = new SDPColor[Constants.INPUT_WIDTH*Constants.INPUT_HEIGHT];
    }

    private int getIndex(int x, int y){
        return y*Constants.INPUT_WIDTH*3 + x*3;
    }

    private void processPixel(int x, int y, SDPColorInstance sdpColorInstance, XYCumulativeAverage average, int maxDepth){
        if(maxDepth <= 0 || x < 0 || x >= Constants.INPUT_WIDTH || y < 0 || y >= Constants.INPUT_HEIGHT) return;
        int i = getIndex(x, y);
        if(this.found[i/3] == sdpColorInstance.sdpColor) return;
        if(sdpColorInstance.isColor(this.hsv[i], this.hsv[i + 1], this.hsv[i + 2])){
            average.addPoint(x, y);
            this.found[i/3] = sdpColorInstance.sdpColor;
            this.processPixel(x-1,y, sdpColorInstance, average, maxDepth - 1);
            this.processPixel(x+1,y, sdpColorInstance, average, maxDepth - 1);
            this.processPixel(x,y+1, sdpColorInstance, average, maxDepth - 1);
            this.processPixel(x,y-1, sdpColorInstance, average, maxDepth - 1);
            Graphics g = Preview.getImageGraphics();
            if(g != null && sdpColorInstance.isVisible()){
                g.setColor(Color.WHITE);
                g.drawRect(x,y,1,1);
            }
        }
    }


    @Override
    public void nextFrame(BufferedImage image, long time) {


        Raster raster = image.getData();

        /*
         * SDP2017NOTE
         * This line right here, right below is the reason our vision system is real time. We fetch the
         * rgb values of the Raster into a preallocated array this.rgb, without allocating more memory.
         * We recycle the memory, so garbage collection is never called.
         */
        raster.getPixels(0, 0, Constants.INPUT_WIDTH, Constants.INPUT_HEIGHT, this.rgb);
        rgbToHsv(this.rgb, this.hsv);

        HashMap<SDPColor, ArrayList<Spot>> spots = new HashMap<SDPColor, ArrayList<Spot>>();
        for(SDPColor c : SDPColor.values()){
            spots.put(c, new ArrayList<Spot>());
        }

        XYCumulativeAverage average = new XYCumulativeAverage();

        HashMap<SDPColor, SDPColorInstance> multiple_region_color = new HashMap<SDPColor, SDPColorInstance>();
        if(DynamicWorld.aliases != null && DynamicWorld.aliases.get(RobotAlias.FRED) != null)
        {
            diag4 = DynamicWorld.aliases.get(RobotAlias.FRED);
            for(int i = 0; i < MultipleRegions.multipleRegions.checkbox_list.size();i++)
            {
                JPanel panel = MultipleRegions.multipleRegions.regions.get(MultipleRegions.multipleRegions.checkbox_list.get(i));
                double x = Integer.valueOf(((JTextField)panel.getComponent(0)).getText()) * 0.47 -  150;
                double y = 110 - (Integer.valueOf(((JTextField)panel.getComponent(1)).getText()) * 0.45);
                double w = Integer.valueOf(((JTextField)panel.getComponent(2)).getText()) * 0.47 ;
                double h = Integer.valueOf(((JTextField)panel.getComponent(3)).getText()) * 0.45 ;
                previ_robot_x = robot_x;
                previ_robot_y = robot_y;
                robot_x = diag4.location.x;
                robot_y = diag4.location.y;
                if(this.contains(x,y,w,h,robot_x,robot_y))
                {
                    multiple_region_color = MultipleRegions.multipleRegions.multiple_region_color.get(MultipleRegions.multipleRegions.checkbox_list.get(i));
                    System.out.println(" we are in region: " + i);
                    break;
                }
            }

            for(int i = 0 ; i < Constants.INPUT_HEIGHT * Constants.INPUT_WIDTH; i++){
                this.found[i] = null;
            }
            for(SDPColor color : SDPColor.values()){
                if(multiple_region_color.isEmpty())
                {
                    colorInstance = SDPColors.sdpColors.colors.get(color);
                }
                else
                {
                    colorInstance = multiple_region_color.get(color);
                }
                for(int y = 0; y < Constants.INPUT_HEIGHT; y++){
                    for(int x = 0; x < Constants.INPUT_WIDTH; x++){
                        this.processPixel(x, y, colorInstance, average, 200);
                        if(average.getCount() > 5){
                            spots.get(color).add(new Spot(average.getXAverage(), average.getYAverage(), average.getCount(), color));
                        }
                        average.reset();
                    }
                }
                Collections.sort(spots.get(color));
            }
        }
        else
        {
            if(previ_robot_x == robot_x && previ_robot_y == robot_y && DynamicWorld.aliases != null)
            {
                if(check == false)
                {
                    map.put(Integer.toString(0),SDPColors.sdpColors.colors);
                    for(int i = 1; i <= MultipleRegions.multipleRegions.checkbox_list.size();i++)
                    {
                        map.put(Integer.toString(i),MultipleRegions.multipleRegions.multiple_region_color.get(MultipleRegions.multipleRegions.checkbox_list.get(i-1)));
                    }
                    check = true;
                }
                for(int i = 0 ; i < Constants.INPUT_HEIGHT * Constants.INPUT_WIDTH; i++){
                    this.found[i] = null;
                }
                while(true) {
                    for (HashMap<SDPColor,SDPColorInstance> sdpColorInstance : map.values()) {
                        for (SDPColor color : SDPColor.values()) {
                            colorInstance = sdpColorInstance.get(color);
                            for (int y = 0; y < Constants.INPUT_HEIGHT; y++) {
                                for (int x = 0; x < Constants.INPUT_WIDTH; x++) {
                                    this.processPixel(x, y, colorInstance, average, 200);
                                    if (average.getCount() > 5) {
                                        spots.get(color).add(new Spot(average.getXAverage(), average.getYAverage(), average.getCount(), color));
                                    }
                                    average.reset();
                                }
                            }
                            Collections.sort(spots.get(color));
                        }
                        if(DynamicWorld.aliases != null && DynamicWorld.aliases.get(RobotAlias.FRED) != null)
                        {
                            diag4 = DynamicWorld.aliases.get(RobotAlias.FRED);
                            previ_robot_x = robot_x;
                            previ_robot_y = robot_y;
                            robot_x = diag4.location.x;
                            robot_y = diag4.location.y;
                            check_break = true;
                            break;
                        }
                    }
                    if(check_break == true)
                    {
                        check_break = false;
                        break;
                    }
                    System.out.println("test");
                }
            }
            else
            {
                for(int i = 0 ; i < Constants.INPUT_HEIGHT * Constants.INPUT_WIDTH; i++){
                    this.found[i] = null;
                }
                if(MultipleRegions.multipleRegions.jCheckBox.isSelected())
                {

                    multiple_region_color = MultipleRegions.multipleRegions.multiple_region_color.get(MultipleRegions.multipleRegions.jCheckBox);
                    for(SDPColor color : SDPColor.values()){
                        colorInstance = multiple_region_color.get(color);
                        for(int y = 0; y < Constants.INPUT_HEIGHT; y++){
                            for(int x = 0; x < Constants.INPUT_WIDTH; x++){
                                this.processPixel(x, y, colorInstance, average, 200);
                                if(average.getCount() > 5){
                                    spots.get(color).add(new Spot(average.getXAverage(), average.getYAverage(), average.getCount(), color));
                                }
                                average.reset();
                            }
                        }
                        Collections.sort(spots.get(color));
                    }
                }
                else
                {
                    for(SDPColor color : SDPColor.values()){
                        colorInstance = SDPColors.sdpColors.colors.get(color);
                        for(int y = 0; y < Constants.INPUT_HEIGHT; y++){
                            for(int x = 0; x < Constants.INPUT_WIDTH; x++){
                                this.processPixel(x, y, colorInstance, average, 200);
                                if(average.getCount() > 5){
                                    spots.get(color).add(new Spot(average.getXAverage(), average.getYAverage(), average.getCount(), color));
                                }
                                average.reset();
                            }
                        }
                        Collections.sort(spots.get(color));
                    }

                }
            }
        }


        this.informListeners(spots, time);
        Preview.flushToLabel();

    }

    public boolean contains(double x, double y, double w, double h, double robot_x, double robot_y)
    {
        return robot_x < (x + w) && robot_x > x && robot_y > (y - h) && robot_y < y;
    }
}
