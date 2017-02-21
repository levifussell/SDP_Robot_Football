package vision.activeVision;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Panel;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vision.gui.Preview;
import vision.tools.ColoredPoint;


/**
 * Created by Siim Sammul
 */
public class OctaveGui extends JPanel implements ChangeListener {
    private JTextField activeThreshTextField;
    private JTextField redThreshTextField;
    private JTextField greenThreshTextField;
    private JTextField blueThreshTextField;
    private JTextField yellowThreshTextField;
    private JTextField pinkThreshTextField;

    private int redThresh = 400001;
    private int activeThresh = 400000;
    private int blueThresh = 400002;
    private int greenThresh = 400003;
    private int pinkThresh = 400004;
    private int yellowThresh = 300005;

    private JSlider activeThreshSlider;
    private JSlider redThreshSlider;
    private JSlider greenThreshSlider;
    private JSlider blueThreshSlider;
    private JSlider yellowThreshSlider;
    private JSlider pinkThreshSlider;

    private Panel activeThreshPanel;
    private Panel redThreshPanel;
    private Panel greenThreshPanel;
    private Panel blueThreshPanel;
    private Panel yellowThreshPanel;
    private Panel pinkThreshPanel;

    public static final OctaveGui octaveGui = new OctaveGui();

    public OctaveGui(){
        super();
        this.setupGUI();
    }

    private void setupGUI(){
        //setTitle("Active Vision Calibration");
        setSize(640,480);
        //JPanel panel = new JPanel();
        //getContentPane().add(panel, BorderLayout.CENTER);
        this.setLayout(null);


        activeThreshSlider = new JSlider();
        activeThreshSlider.setBounds(183, 56, 200, 14);
        this.add(activeThreshSlider);

        activeThreshPanel = new Panel();
        activeThreshPanel.setBounds(140, 56, 37, 14);
        this.add(activeThreshPanel);

        JLabel activeThreshLbl = new JLabel("Active Thresh:");
        activeThreshLbl.setBounds(10, 56, 120, 14);
        this.add(activeThreshLbl);

        greenThreshSlider = new JSlider();
        greenThreshSlider.setBounds(183, 137, 200, 14);
        this.add(greenThreshSlider);

        greenThreshPanel = new Panel();
        greenThreshPanel.setBounds(140, 137, 37, 14);
        this.add(greenThreshPanel);

        JLabel greenThreshLbl = new JLabel("Green Thresh:");
        greenThreshLbl.setBounds(10, 137, 120, 14);
        this.add(greenThreshLbl);

        blueThreshSlider = new JSlider();
        blueThreshSlider.setBounds(183, 176, 200, 14);
        this.add(blueThreshSlider);

        blueThreshPanel = new Panel();
        blueThreshPanel.setBounds(140, 176, 37, 14);
        this.add(blueThreshPanel);

        JLabel blueThreshLbl = new JLabel("Blue Thresh:");
        blueThreshLbl.setBounds(10, 176, 120, 14);
        this.add(blueThreshLbl);

        yellowThreshSlider = new JSlider();
        yellowThreshSlider.setBounds(183, 215, 200, 14);
        this.add(yellowThreshSlider);

        yellowThreshPanel = new Panel();
        yellowThreshPanel.setBounds(140, 215, 37, 14);
        this.add(yellowThreshPanel);

        JLabel yellowThreshLbl = new JLabel("Yellow Thresh:");
        yellowThreshLbl.setBounds(10, 215, 120, 14);
        this.add(yellowThreshLbl);

        pinkThreshSlider = new JSlider();
        pinkThreshSlider.setBounds(183, 254, 200, 14);
        this.add(pinkThreshSlider);

        pinkThreshPanel = new Panel();
        pinkThreshPanel.setBounds(140, 254, 37, 14);
        this.add(pinkThreshPanel);

        JLabel pinkThreshLbl = new JLabel("Pink Thresh:");
        pinkThreshLbl.setBounds(10, 254, 120, 14);
        this.add(pinkThreshLbl);

        activeThreshTextField = new JTextField();
        activeThreshTextField.setBounds(389, 39, 86, 26);
        this.add(activeThreshTextField);
        activeThreshTextField.setColumns(10);

        greenThreshTextField = new JTextField();
        greenThreshTextField.setColumns(10);
        greenThreshTextField.setBounds(389, 123, 86, 26);
        this.add(greenThreshTextField);

        blueThreshTextField = new JTextField();
        blueThreshTextField.setColumns(10);
        blueThreshTextField.setBounds(389, 162, 86, 26);
        this.add(blueThreshTextField);

        yellowThreshTextField = new JTextField();
        yellowThreshTextField.setColumns(10);
        yellowThreshTextField.setBounds(389, 201, 86, 26);
        this.add(yellowThreshTextField);

        pinkThreshTextField = new JTextField();
        pinkThreshTextField.setColumns(10);
        pinkThreshTextField.setBounds(389, 240, 86, 26);
        this.add(pinkThreshTextField);

        JLabel redThreshLbl = new JLabel("Red Thresh:");
        redThreshLbl.setBounds(10, 98, 120, 14);
        this.add(redThreshLbl);

        redThreshPanel = new Panel();
        redThreshPanel.setBounds(140, 98, 37, 14);
        this.add(redThreshPanel);

        redThreshSlider = new JSlider();
        redThreshSlider.setBounds(183, 98, 200, 14);
        this.add(redThreshSlider);

        redThreshTextField = new JTextField();
        redThreshTextField.setColumns(10);
        redThreshTextField.setBounds(389, 81, 86, 26);
        this.add(redThreshTextField);

        this.activeThreshSlider.setMaximum(600000);
        this.activeThreshSlider.setMinimum(200000);
        this.redThreshSlider.setMaximum(600000);
        this.redThreshSlider.setMinimum(200000);
        this.greenThreshSlider.setMaximum(600000);
        this.greenThreshSlider.setMinimum(200000);
        this.blueThreshSlider.setMaximum(600000);
        this.blueThreshSlider.setMinimum(200000);
        this.yellowThreshSlider.setMaximum(600000);
        this.yellowThreshSlider.setMinimum(200000);
        this.pinkThreshSlider.setMaximum(600000);
        this.pinkThreshSlider.setMinimum(200000);
        this.activeThreshSlider.addChangeListener(this);
        this.redThreshSlider.addChangeListener(this);
        this.greenThreshSlider.addChangeListener(this);
        this.blueThreshSlider.addChangeListener(this);
        this.yellowThreshSlider.addChangeListener(this);
        this.pinkThreshSlider.addChangeListener(this);
        this.recalculateSliders();
        redThresh = 400001;
        this.recalculateSliders();
        activeThresh = 400000;
        this.recalculateSliders();
        blueThresh = 400002;
        this.recalculateSliders();
        greenThresh = 400003;
        this.recalculateSliders();
        pinkThresh = 400004;
        this.recalculateSliders();
        yellowThresh = 300005;
        this.recalculateSliders();
        this.myRepaint();
//        this.setVisible(false);
    }

    private void recalculateValues(){
        activeThresh = activeThreshSlider.getValue();
        redThresh = redThreshSlider.getValue();
        blueThresh = blueThreshSlider.getValue();
        greenThresh = greenThreshSlider.getValue();
        pinkThresh = pinkThreshSlider.getValue();
        yellowThresh = yellowThreshSlider.getValue();
    }

    private void recalculateSliders(){
        activeThreshSlider.setValue(activeThresh);
        redThreshSlider.setValue(redThresh);
        blueThreshSlider.setValue(blueThresh);
        greenThreshSlider.setValue(greenThresh);
        pinkThreshSlider.setValue(pinkThresh);
        yellowThreshSlider.setValue(yellowThresh);
    }

    public void myRepaint(){
        this.activeThreshTextField.setText("" + this.activeThresh);
        this.redThreshTextField.setText("" + this.redThresh);
        this.blueThreshTextField.setText("" + this.blueThresh);
        this.greenThreshTextField.setText("" + this.greenThresh);
        this.pinkThreshTextField.setText("" + this.pinkThresh);
        this.yellowThreshTextField.setText("" + this.yellowThresh);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        this.recalculateValues();
        this.myRepaint();
    }

    public int getRedThresh() {
        return redThresh;
    }

    public int getActiveThresh() {
        return activeThresh;
    }

    public int getBlueThresh() {
        return blueThresh;
    }

    public int getGreenThresh() {
        return greenThresh;
    }

    public int getPinkThresh() {
        return pinkThresh;
    }

    public int getYellowThresh() {
        return yellowThresh;
    }
}
