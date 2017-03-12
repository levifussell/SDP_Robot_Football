package backgroundSub;

import org.opencv.core.Scalar;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Panel;

/**
 * Created by jinhong on 11/03/2017.
 */
public class OpenCVGUI extends JPanel implements ChangeListener{
    private int redLower_H=0,redLower_S=0,redLower_V=0;
    private int redUpper_H=0,redUpper_S=0,redUpper_V=0;

    private int yellowLower_H=0,yellowLower_S=0,yellowLower_V=0;
    private int yellowUpper_H=0,yellowUpper_S=0,yellowUpper_V=0;

    private int blueLower_H=0,blueLower_S=0,blueLower_V=0;
    private int blueUpper_H=0,blueUpper_S=0,blueUpper_V=0;

    private int greenLower_H=0,greenLower_S=0,greenLower_V=0;
    private int greenUpper_H=0,greenUpper_S=0,greenUpper_V=0;

    private JTextField redLower_H_Text,redLower_S_Text,redLower_V_Text;
    private JTextField redUpper_H_Text,redUpper_S_Text,redUpper_V_Text;

    private JTextField yellowLower_H_Text,yellowLower_S_Text,yellowLower_V_Text;
    private JTextField yellowUpper_H_Text,yellowUpper_S_Text,yellowUpper_V_Text;

    private JTextField blueLower_H_Text,blueLower_S_Text,blueLower_V_Text;
    private JTextField blueUpper_H_Text,blueUpper_S_Text,blueUpper_V_Text;

    private JTextField greenLower_H_Text,greenLower_S_Text,greenLower_V_Text;
    private JTextField greenUpper_H_Text,greenUpper_S_Text,greenUpper_V_Text;

    private JSlider redLower_H_Slider,redLower_S_Slider,redLower_V_Slider;
    private JSlider redUpper_H_Slider,redUpper_S_Slider,redUpper_V_Slider;

    private JSlider yellowLower_H_Slider,yellowLower_S_Slider,yellowLower_V_Slider;
    private JSlider yellowUpper_H_Slider,yellowUpper_S_Slider,yellowUpper_V_Slider;

    private JSlider blueLower_H_Slider,blueLower_S_Slider,blueLower_V_Slider;
    private JSlider blueUpper_H_Slider,blueUpper_S_Slider,blueUpper_V_Slider;

    private JSlider greenLower_H_Slider,greenLower_S_Slider,greenLower_V_Slider;
    private JSlider greenUpper_H_Slider,greenUpper_S_Slider,greenUpper_V_Slider;

    private Panel redLower_H_Panel,redLower_S_Panel,redLower_V_Panel;
    private Panel redUpper_H_Panel,redUpper_S_Panel,redUpper_V_Panel;

    private Panel yellowLower_H_Panel,yellowLower_S_Panel,yellowLower_V_Panel;
    private Panel yellowUpper_H_Panel,yellowUpper_S_Panel,yellowUpper_V_Panel;

    private Panel blueLower_H_Panel,blueLower_S_Panel,blueLower_V_Panel;
    private Panel blueUpper_H_Panel,blueUpper_S_Panel,blueUpper_V_Panel;

    private Panel greenLower_H_Panel,greenLower_S_Panel,greenLower_V_Panel;
    private Panel greenUpper_H_Panel,greenUpper_S_Panel,greenUpper_V_Panel;


    private Scalar red_Lower_HSV,red_Upper_HSV;
    private Scalar yellow_Lower_HSV,yellow_Upper_HSV;
    private Scalar blue_Lower_HSV,blue_Upper_HSV;
    private Scalar green_Lower_HSV,green_Upper_HSV;

    public static final OpenCVGUI opencvGUI = new OpenCVGUI();

    public OpenCVGUI()
    {
        super();
        this.setupGUI();
    }

    private void setupGUI()
    {
        setSize(640,480);

        this.setLayout(null);

        redLower_H_Slider = new JSlider();
        redLower_H_Slider.setBounds(183,56,255,14);
        this.add(redLower_H_Slider);

        redLower_H_Panel = new Panel();
        redLower_H_Panel.setBounds(140,56,37,14);
        this.add(redLower_H_Panel);

        JLabel redLower_H_Label = new JLabel("Red Lower H");
        redLower_H_Label.setBounds(10,56,120,14);
        this.add(redLower_H_Label);


        redLower_S_Slider = new JSlider();
        redLower_S_Slider.setBounds(183,72,255,14);
        this.add(redLower_S_Slider);

        redLower_S_Panel = new Panel();
        redLower_S_Panel.setBounds(140,72,37,14);
        this.add(redLower_S_Panel);

        JLabel redLower_S_Label = new JLabel("Red Lower S");
        redLower_S_Label.setBounds(10,72,120,14);
        this.add(redLower_S_Label);



        redLower_V_Slider = new JSlider();
        redLower_V_Slider.setBounds(183,88,255,14);
        this.add(redLower_V_Slider);

        redLower_V_Panel = new Panel();
        redLower_V_Panel.setBounds(140,88,37,14);
        this.add(redLower_V_Panel);

        JLabel redLower_V_Label = new JLabel("Red Lower V");
        redLower_V_Label.setBounds(10,88,120,14);
        this.add(redLower_V_Label);



        redUpper_H_Slider = new JSlider();
        redUpper_H_Slider.setBounds(183,104,255,14);
        this.add(redUpper_H_Slider);

        redUpper_H_Panel = new Panel();
        redUpper_H_Panel.setBounds(140,104,37,14);
        this.add(redUpper_H_Panel);

        JLabel redUpper_H_Label = new JLabel("Red Upper H");
        redUpper_H_Label.setBounds(10,104,120,14);
        this.add(redUpper_H_Label);


        redUpper_S_Slider = new JSlider();
        redUpper_S_Slider.setBounds(183,120,255,14);
        this.add(redUpper_S_Slider);

        redUpper_S_Panel = new Panel();
        redUpper_S_Panel.setBounds(140,120,37,14);
        this.add(redUpper_S_Panel);

        JLabel redUpper_S_Label = new JLabel("Red Upper S");
        redUpper_S_Label.setBounds(10,120,120,14);
        this.add(redUpper_S_Label);


        redUpper_V_Slider = new JSlider();
        redUpper_V_Slider.setBounds(183,136,255,14);
        this.add(redUpper_V_Slider);

        redUpper_V_Panel = new Panel();
        redUpper_V_Panel.setBounds(140,136,37,14);
        this.add(redUpper_V_Panel);

        JLabel redUpper_V_Label = new JLabel("Red Upper V");
        redUpper_V_Label.setBounds(10,136,120,14);
        this.add(redUpper_V_Label);





        yellowLower_H_Slider = new JSlider();
        yellowLower_H_Slider.setBounds(183,152,255,14);
        this.add(yellowLower_H_Slider);

        yellowLower_H_Panel = new Panel();
        yellowLower_H_Panel.setBounds(140,152,37,14);
        this.add(yellowLower_H_Panel);

        JLabel yellowLower_H_Label = new JLabel("Yellow Lower H");
        yellowLower_H_Label.setBounds(10,152,120,14);
        this.add(yellowLower_H_Label);


        yellowLower_S_Slider = new JSlider();
        yellowLower_S_Slider.setBounds(183,168,255,14);
        this.add(yellowLower_S_Slider);

        yellowLower_S_Panel = new Panel();
        yellowLower_S_Panel.setBounds(140,168,37,14);
        this.add(yellowLower_S_Panel);

        JLabel yellowLower_S_Label = new JLabel("Yellow Lower S");
        yellowLower_S_Label.setBounds(10,168,120,14);
        this.add(yellowLower_S_Label);


        yellowLower_V_Slider = new JSlider();
        yellowLower_V_Slider.setBounds(183,184,255,14);
        this.add(yellowLower_V_Slider);

        yellowLower_V_Panel = new Panel();
        yellowLower_V_Panel.setBounds(140,184,37,14);
        this.add(yellowLower_V_Panel);

        JLabel yellowLower_V_Label = new JLabel("Yellow Lower V");
        yellowLower_V_Label.setBounds(10,184,120,14);
        this.add(yellowLower_V_Label);



        yellowUpper_H_Slider = new JSlider();
        yellowUpper_H_Slider.setBounds(183,200,255,14);
        this.add(yellowUpper_H_Slider);

        yellowUpper_H_Panel = new Panel();
        yellowUpper_H_Panel.setBounds(140,200,37,14);
        this.add(yellowUpper_H_Panel);

        JLabel yellowUpper_H_Label = new JLabel("Yellow Upper H");
        yellowUpper_H_Label.setBounds(10,200,120,14);
        this.add(yellowUpper_H_Label);

        yellowUpper_S_Slider = new JSlider();
        yellowUpper_S_Slider.setBounds(183,216,255,14);
        this.add(yellowUpper_S_Slider);

        yellowUpper_S_Panel = new Panel();
        yellowUpper_S_Panel.setBounds(140,216,37,14);
        this.add(yellowUpper_S_Panel);

        JLabel yellowUpper_S_Label = new JLabel("Yellow Upper S");
        yellowUpper_S_Label.setBounds(10,216,120,14);
        this.add(yellowUpper_S_Label);

        yellowUpper_V_Slider = new JSlider();
        yellowUpper_V_Slider.setBounds(183,232,255,14);
        this.add(yellowUpper_V_Slider);

        yellowUpper_V_Panel = new Panel();
        yellowUpper_V_Panel.setBounds(140,232,37,14);
        this.add(yellowUpper_V_Panel);

        JLabel yellowUpper_V_Label = new JLabel("Yellow Upper V");
        yellowUpper_V_Label.setBounds(10,232,120,14);
        this.add(yellowUpper_V_Label);






        blueLower_H_Slider = new JSlider();
        blueLower_H_Slider.setBounds(183,248,255,14);
        this.add(blueLower_H_Slider);

        blueLower_H_Panel = new Panel();
        blueLower_H_Panel.setBounds(140,248,37,14);
        this.add(blueLower_H_Panel);

        JLabel blueLower_H_Label = new JLabel("Blue Lower H");
        blueLower_H_Label.setBounds(10,248,120,14);
        this.add(blueLower_H_Label);

        blueLower_S_Slider = new JSlider();
        blueLower_S_Slider.setBounds(183,264,255,14);
        this.add(blueLower_S_Slider);

        blueLower_S_Panel = new Panel();
        blueLower_S_Panel.setBounds(140,264,37,14);
        this.add(blueLower_S_Panel);

        JLabel blueLower_S_Label = new JLabel("Blue Lower S");
        blueLower_S_Label.setBounds(10,264,120,14);
        this.add(blueLower_S_Label);

        blueLower_V_Slider = new JSlider();
        blueLower_V_Slider.setBounds(183,280,255,14);
        this.add(blueLower_V_Slider);

        blueLower_V_Panel = new Panel();
        blueLower_V_Panel.setBounds(140,280,37,14);
        this.add(blueLower_V_Panel);

        JLabel blueLower_V_Label = new JLabel("Blue Lower V");
        blueLower_V_Label.setBounds(10,280,120,14);
        this.add(blueLower_V_Label);


        blueUpper_H_Slider = new JSlider();
        blueUpper_H_Slider.setBounds(183,296,255,14);
        this.add(blueUpper_H_Slider);

        blueUpper_H_Panel = new Panel();
        blueUpper_H_Panel.setBounds(140,296,37,14);
        this.add(blueUpper_H_Panel);

        JLabel blueUpper_H_Label = new JLabel("Blue Upper H");
        blueUpper_H_Label.setBounds(10,296,120,14);
        this.add(blueUpper_H_Label);

        blueUpper_S_Slider = new JSlider();
        blueUpper_S_Slider.setBounds(183,312,255,14);
        this.add(blueUpper_S_Slider);

        blueUpper_S_Panel = new Panel();
        blueUpper_S_Panel.setBounds(140,312,37,14);
        this.add(blueUpper_S_Panel);

        JLabel blueUpper_S_Label = new JLabel("Blue Upper S");
        blueUpper_S_Label.setBounds(10,312,120,14);
        this.add(blueUpper_S_Label);

        blueUpper_V_Slider = new JSlider();
        blueUpper_V_Slider.setBounds(183,328,255,14);
        this.add(blueUpper_V_Slider);

        blueUpper_V_Panel = new Panel();
        blueUpper_V_Panel.setBounds(140,328,37,14);
        this.add(blueUpper_V_Panel);

        JLabel blueUpper_V_Label = new JLabel("Blue Upper V");
        blueUpper_V_Label.setBounds(10,328,120,14);
        this.add(blueUpper_V_Label);





        greenLower_H_Slider = new JSlider();
        greenLower_H_Slider.setBounds(183,344,255,14);
        this.add(greenLower_H_Slider);

        greenLower_H_Panel = new Panel();
        greenLower_H_Panel.setBounds(140,344,37,14);
        this.add(greenLower_H_Panel);

        JLabel greenLower_H_Label = new JLabel("Green Lower H");
        greenLower_H_Label.setBounds(10,344,120,14);
        this.add(greenLower_H_Label);

        greenLower_S_Slider = new JSlider();
        greenLower_S_Slider.setBounds(183,360,255,14);
        this.add(greenLower_S_Slider);

        greenLower_S_Panel = new Panel();
        greenLower_S_Panel.setBounds(140,360,37,14);
        this.add(greenLower_S_Panel);

        JLabel greenLower_S_Label = new JLabel("Green Lower S");
        greenLower_S_Label.setBounds(10,360,120,14);
        this.add(greenLower_S_Label);

        greenLower_V_Slider = new JSlider();
        greenLower_V_Slider.setBounds(183,376,255,14);
        this.add(greenLower_V_Slider);

        greenLower_V_Panel = new Panel();
        greenLower_V_Panel.setBounds(140,376,37,14);
        this.add(greenLower_V_Panel);

        JLabel greenLower_V_Label = new JLabel("Green Lower V");
        greenLower_V_Label.setBounds(10,376,120,14);
        this.add(greenLower_V_Label);


        greenUpper_H_Slider = new JSlider();
        greenUpper_H_Slider.setBounds(183,392,255,14);
        this.add(greenUpper_H_Slider);

        greenUpper_H_Panel = new Panel();
        greenUpper_H_Panel.setBounds(140,392,37,14);
        this.add(greenUpper_H_Panel);

        JLabel greenUpper_H_Label = new JLabel("Green Upper H");
        greenUpper_H_Label.setBounds(10,392,120,14);
        this.add(greenUpper_H_Label);

        greenUpper_S_Slider = new JSlider();
        greenUpper_S_Slider.setBounds(183,408,255,14);
        this.add(greenUpper_S_Slider);

        greenUpper_S_Panel = new Panel();
        greenUpper_S_Panel.setBounds(140,408,37,14);
        this.add(greenUpper_H_Panel);

        JLabel greenUpper_S_Label = new JLabel("Green Upper S");
        greenUpper_S_Label.setBounds(10,408,120,14);
        this.add(greenUpper_S_Label);

        greenUpper_V_Slider = new JSlider();
        greenUpper_V_Slider.setBounds(183,424,255,14);
        this.add(greenUpper_V_Slider);

        greenUpper_V_Panel = new Panel();
        greenUpper_V_Panel.setBounds(140,424,37,14);
        this.add(greenUpper_V_Panel);

        JLabel greenUpper_V_Label = new JLabel("Green Upper V");
        greenUpper_V_Label.setBounds(10,424,120,14);
        this.add(greenUpper_V_Label);

        this.redLower_H_Slider.setMinimum(0);
        this.redLower_H_Slider.setMaximum(255);
        this.redLower_S_Slider.setMinimum(0);
        this.redLower_S_Slider.setMaximum(255);
        this.redLower_V_Slider.setMinimum(0);
        this.redLower_V_Slider.setMaximum(255);

        this.redUpper_H_Slider.setMinimum(0);
        this.redUpper_H_Slider.setMaximum(255);
        this.redUpper_S_Slider.setMinimum(0);
        this.redUpper_S_Slider.setMaximum(255);
        this.redUpper_V_Slider.setMinimum(0);
        this.redUpper_V_Slider.setMaximum(255);

        this.yellowLower_H_Slider.setMinimum(0);
        this.yellowLower_H_Slider.setMaximum(255);
        this.yellowLower_S_Slider.setMinimum(0);
        this.yellowLower_S_Slider.setMaximum(255);
        this.yellowLower_V_Slider.setMinimum(0);
        this.yellowLower_V_Slider.setMaximum(255);

        this.yellowUpper_H_Slider.setMinimum(0);
        this.yellowUpper_H_Slider.setMaximum(255);
        this.yellowUpper_S_Slider.setMinimum(0);
        this.yellowUpper_S_Slider.setMaximum(255);
        this.yellowUpper_V_Slider.setMinimum(0);
        this.yellowUpper_V_Slider.setMaximum(255);

        this.blueLower_H_Slider.setMinimum(0);
        this.blueLower_H_Slider.setMaximum(255);
        this.blueLower_S_Slider.setMinimum(0);
        this.blueLower_S_Slider.setMaximum(255);
        this.blueLower_V_Slider.setMinimum(0);
        this.blueLower_V_Slider.setMaximum(255);

        this.blueUpper_H_Slider.setMinimum(0);
        this.blueUpper_H_Slider.setMaximum(255);
        this.blueUpper_S_Slider.setMinimum(0);
        this.blueUpper_S_Slider.setMaximum(255);
        this.blueUpper_V_Slider.setMinimum(0);
        this.blueUpper_V_Slider.setMaximum(255);

        this.greenLower_H_Slider.setMinimum(0);
        this.greenLower_H_Slider.setMaximum(255);
        this.greenLower_S_Slider.setMinimum(0);
        this.greenLower_S_Slider.setMaximum(255);
        this.greenLower_V_Slider.setMinimum(0);
        this.greenLower_V_Slider.setMaximum(255);

        this.greenUpper_H_Slider.setMinimum(0);
        this.greenUpper_H_Slider.setMaximum(255);
        this.greenUpper_S_Slider.setMinimum(0);
        this.greenUpper_S_Slider.setMaximum(255);
        this.greenUpper_V_Slider.setMinimum(0);
        this.greenUpper_V_Slider.setMaximum(255);

        this.redLower_H_Slider.addChangeListener(this);
        this.redLower_S_Slider.addChangeListener(this);
        this.redLower_V_Slider.addChangeListener(this);

        this.redUpper_H_Slider.addChangeListener(this);
        this.redUpper_S_Slider.addChangeListener(this);
        this.redUpper_V_Slider.addChangeListener(this);

        this.yellowLower_H_Slider.addChangeListener(this);
        this.yellowLower_S_Slider.addChangeListener(this);
        this.yellowLower_V_Slider.addChangeListener(this);

        this.yellowUpper_H_Slider.addChangeListener(this);
        this.yellowUpper_S_Slider.addChangeListener(this);
        this.yellowUpper_V_Slider.addChangeListener(this);

        this.blueLower_H_Slider.addChangeListener(this);
        this.blueLower_S_Slider.addChangeListener(this);
        this.blueLower_V_Slider.addChangeListener(this);

        this.blueUpper_H_Slider.addChangeListener(this);
        this.blueUpper_S_Slider.addChangeListener(this);
        this.blueUpper_V_Slider.addChangeListener(this);

        this.greenLower_H_Slider.addChangeListener(this);
        this.greenLower_S_Slider.addChangeListener(this);
        this.greenLower_V_Slider.addChangeListener(this);

        this.greenUpper_H_Slider.addChangeListener(this);
        this.greenUpper_S_Slider.addChangeListener(this);
        this.greenUpper_V_Slider.addChangeListener(this);

        this.recalculateSliders();

        //this.myRepaint();

    }

    private void recalculateValues()
    {
        redLower_H = redLower_H_Slider.getValue();
        redLower_S = redLower_S_Slider.getValue();
        redLower_V = redLower_V_Slider.getValue();

        redUpper_H = redUpper_H_Slider.getValue();
        redUpper_S = redUpper_S_Slider.getValue();
        redUpper_V = redUpper_V_Slider.getValue();

        yellowLower_H = yellowLower_H_Slider.getValue();
        yellowLower_S = yellowLower_S_Slider.getValue();
        yellowLower_V = yellowLower_V_Slider.getValue();

        yellowUpper_H = yellowUpper_H_Slider.getValue();
        yellowUpper_S = yellowUpper_S_Slider.getValue();
        yellowUpper_V = yellowUpper_V_Slider.getValue();

        blueLower_H = blueLower_H_Slider.getValue();
        blueLower_S = blueLower_S_Slider.getValue();
        blueLower_V = blueLower_V_Slider.getValue();

        blueUpper_H = blueUpper_H_Slider.getValue();
        blueUpper_S = blueUpper_S_Slider.getValue();
        blueUpper_V = blueUpper_V_Slider.getValue();

        greenLower_H = greenLower_H_Slider.getValue();
        greenLower_S = greenLower_S_Slider.getValue();
        greenLower_V = greenLower_V_Slider.getValue();

        greenUpper_H = greenUpper_H_Slider.getValue();
        greenUpper_S = greenUpper_S_Slider.getValue();
        greenUpper_V = greenUpper_V_Slider.getValue();
    }

    private void recalculateSliders()
    {
        redLower_H_Slider.setValue(redLower_H);
        redLower_S_Slider.setValue(redLower_S);
        redLower_V_Slider.setValue(redLower_V);

        redUpper_H_Slider.setValue(redUpper_H);
        redUpper_S_Slider.setValue(redUpper_S);
        redUpper_V_Slider.setValue(redUpper_V);

        yellowLower_H_Slider.setValue(yellowLower_H);
        yellowLower_S_Slider.setValue(yellowLower_S);
        yellowLower_V_Slider.setValue(yellowLower_V);

        yellowUpper_H_Slider.setValue(yellowUpper_H);
        yellowUpper_S_Slider.setValue(yellowUpper_S);
        yellowUpper_V_Slider.setValue(yellowUpper_V);

        blueLower_H_Slider.setValue(blueLower_H);
        blueLower_S_Slider.setValue(blueLower_S);
        blueLower_V_Slider.setValue(blueLower_V);

        blueUpper_H_Slider.setValue(blueUpper_H);
        blueUpper_S_Slider.setValue(blueUpper_S);
        blueUpper_V_Slider.setValue(blueUpper_V);

        greenLower_H_Slider.setValue(greenLower_H);
        greenLower_S_Slider.setValue(greenLower_S);
        greenLower_V_Slider.setValue(greenLower_V);

        greenUpper_H_Slider.setValue(greenUpper_H);
        greenUpper_S_Slider.setValue(greenUpper_S);
        greenUpper_V_Slider.setValue(greenUpper_V);
    }

//    public void myRepaint()
//    {
//        this.redLower_H_Text.setText("" + this.redLower_H);
//        this.redLower_S_Text.setText("" + this.redLower_S);
//        this.redLower_V_Text.setText("" + this.redLower_V);
//
//        this.redUpper_H_Text.setText("" + this.redUpper_H);
//        this.redUpper_S_Text.setText("" + this.redUpper_S);
//        this.redUpper_V_Text.setText("" + this.redUpper_V);
//
//        this.yellowLower_H_Text.setText("" + this.yellowLower_H);
//        this.yellowLower_S_Text.setText("" + this.yellowLower_S);
//        this.yellowLower_V_Text.setText("" + this.yellowLower_V);
//
//        this.yellowUpper_H_Text.setText("" + this.yellowUpper_H);
//        this.yellowUpper_S_Text.setText("" + this.yellowUpper_S);
//        this.yellowUpper_V_Text.setText("" + this.yellowUpper_V);
//
//        this.blueLower_H_Text.setText("" + this.blueLower_H);
//        this.blueLower_S_Text.setText("" + this.blueLower_S);
//        this.blueLower_V_Text.setText("" + this.blueLower_V);
//
//        this.blueUpper_H_Text.setText("" + this.blueUpper_H);
//        this.blueUpper_S_Text.setText("" + this.blueUpper_S);
//        this.blueUpper_V_Text.setText("" + this.blueUpper_V);
//
//        this.greenLower_H_Text.setText("" + this.greenLower_H);
//        this.greenLower_S_Text.setText("" + this.greenLower_S);
//        this.greenLower_V_Text.setText("" + this.greenLower_V);
//
//        this.greenUpper_H_Text.setText("" + this.greenUpper_H);
//        this.greenUpper_S_Text.setText("" + this.greenUpper_S);
//        this.greenUpper_V_Text.setText("" + this.greenUpper_V);
//    }

    @Override
    public void stateChanged(ChangeEvent e) {
        this.recalculateValues();
        //this.myRepaint();

    }

    public Scalar getRedLower()
    {
        red_Lower_HSV = new Scalar(redLower_H,redLower_S,redLower_V);
        return red_Lower_HSV;
    }

    public Scalar getRedUpper()
    {
        red_Upper_HSV = new Scalar(redUpper_H,redUpper_S,redUpper_V);
        return red_Upper_HSV;
    }

    public Scalar getYellowLower()
    {
        yellow_Lower_HSV = new Scalar(yellowLower_H,yellowLower_S,yellowLower_V);
        return yellow_Lower_HSV;
    }

    public Scalar getYellowUpper()
    {
        yellow_Upper_HSV = new Scalar(yellowUpper_H,yellowUpper_S,yellowUpper_V);
        return yellow_Upper_HSV;
    }

    public Scalar getBlueLower()
    {
        blue_Lower_HSV = new Scalar(blueLower_H,blueLower_S,blueLower_V);
        return blue_Lower_HSV;
    }

    public Scalar getBlueUpper()
    {
        blue_Upper_HSV = new Scalar(blueUpper_H,blueUpper_S,blueUpper_V);
        return blue_Upper_HSV;
    }

    public Scalar getGreenLower()
    {
        green_Lower_HSV = new Scalar(greenLower_H,greenLower_S,greenLower_V);
        return green_Lower_HSV;
    }

    public Scalar getGreenUpper()
    {
        green_Upper_HSV = new Scalar(greenUpper_H,greenUpper_S,greenUpper_V);
        return green_Upper_HSV;
    }
}
