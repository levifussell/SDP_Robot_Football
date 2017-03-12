package strategy;

import strategy.controllers.essentials.MotionController;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.InFrontOfRobot;
import strategy.points.basicPoints.MidPoint;
import strategy.points.basicPoints.RobotPoint;
import strategy.robots.Diag4;
import strategy.robots.RobotBase;
import vision.RobotAlias;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Simon Rovder
 */
public class GUI extends JFrame implements KeyListener{

    public JTextField action;
    public JTextField searchType;
    public JTextField behaviour;
    private JTextField r;
    private JTextField maxSpeed;
    private JTextField turnSpeed;

    public static final GUI gui = new GUI();

    private GUI(){
        super("Strategy");
        this.setSize(640,480);
        this.setLayout(null);
        Container c = this.getContentPane();


        JLabel label = new JLabel("Action:");
        label.setBounds(20,20,200,30);
        c.add(label);

        this.action = new JTextField();
        this.action.setBounds(220,20,300,30);
        this.action.setEditable(false);
        c.add(this.action);


        label = new JLabel("NavigationInterface:");
        label.setBounds(20,60,200,30);
        c.add(label);

        this.searchType = new JTextField();
        this.searchType.setBounds(220,60,300,30);
        this.searchType.setEditable(false);
        c.add(this.searchType);


        label = new JLabel("Behavior Mode:");
        label.setBounds(20,100,200,30);
        c.add(label);

        this.behaviour = new JTextField();
        this.behaviour.setBounds(220,100,300,30);
        this.behaviour.setEditable(false);
        c.add(this.behaviour);
        this.addKeyListener(this);


//
//
//
//
//        this.behaviour = new JTextField();
//        this.behaviour.setBounds(20,100,300,30);
//        this.behaviour.setEditable(false);
//        c.add(this.behaviour);
//        this.addKeyListener(this);
        this.setVisible(true);





        label = new JLabel("Maximum Speed:");
        label.setBounds(20,140,200,30);
        c.add(label);
        this.maxSpeed = new JTextField();
        this.maxSpeed.setBounds(220,140,300,30);
        this.maxSpeed.setText("100");
        c.add(this.maxSpeed);
        this.maxSpeed.addKeyListener(this);


        label = new JLabel("Maximum rotation speed:");
        label.setBounds(20,180,200,30);
        c.add(label);
        this.turnSpeed = new JTextField();
        this.turnSpeed.setBounds(220,180,300,30);
        this.turnSpeed.setText("30");
        c.add(this.turnSpeed);
        this.turnSpeed.addKeyListener(this);


        label = new JLabel("Command box:");
        label.setBounds(20,250,200,30);
        c.add(label);
        r = new JTextField();
        r.setBounds(220,250,300,30);
        c.add(r);
        r.addKeyListener(this);

    }


    public void doesNothingButIsNecessarySoDontDelete(){}

    @Override
    public void keyTyped(KeyEvent e) {
    }


    private RobotBase robot;

    public void setRobot(RobotBase robot){
        this.robot = robot;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getSource() == this.r){
            this.robot.MOTION_CONTROLLER.setMode(MotionController.MotionMode.ON);
            this.robot.MOTION_CONTROLLER.setHeading(null);
            this.robot.MOTION_CONTROLLER.setDestination(null);
            ((Diag4)this.robot).SPINNERKICK_CONTROLLER.setActive(false);
            ((Diag4)this.robot).SPINNERKICK_CONTROLLER.disengageSpinner(true);
            ((Diag4)this.robot).SPINNERKICK_CONTROLLER.disengageSpinner(true);
            ((Diag4)this.robot).SPINNERKICK_CONTROLLER.disengageSpinner(true);
            this.robot.port.sdpPort.commandSender("f");
            this.robot.port.sdpPort.commandSender("f");
            this.robot.port.sdpPort.commandSender("f");
            switch(e.getKeyChar()){
                case 'a':
                    this.robot.setControllersActive(true);
                    this.robot.MOTION_CONTROLLER.setDestination(new InFrontOfRobot(RobotAlias.FELIX));
                    this.robot.MOTION_CONTROLLER.setHeading(new RobotPoint(RobotAlias.FELIX));
                    break;
                case 'q':
                    this.robot.setControllersActive(true);
                    this.robot.MOTION_CONTROLLER.setDestination(new InFrontOfRobot(RobotAlias.JEFFREY));
                    this.robot.MOTION_CONTROLLER.setHeading(new RobotPoint(RobotAlias.JEFFREY));
                    break;
                case 'o':
                    this.robot.setControllersActive(true);
                    this.robot.MOTION_CONTROLLER.setDestination(new MidPoint(new RobotPoint(RobotAlias.FELIX), new BallPoint()));
                    this.robot.MOTION_CONTROLLER.setHeading(new RobotPoint(RobotAlias.FELIX));
                    break;
                case 'p':
                    this.robot.setControllersActive(true);
                    this.robot.MOTION_CONTROLLER.setDestination(new MidPoint(new RobotPoint(RobotAlias.JEFFREY), new BallPoint()));
                    this.robot.MOTION_CONTROLLER.setHeading(new RobotPoint(RobotAlias.JEFFREY));
                    break;
                case 'd':
                    this.robot.setControllersActive(true);
                    break;
                case 'k':
                    this.robot.setControllersActive(true);
<<<<<<< HEAD
                    for(int i =0; i<10;i++) {
                        this.robot.port.sdpPort.commandSender("i");
                    }
                    this.robot.ACTION_CONTROLLER.setAction(new OffensiveKick(this.robot));
=======
>>>>>>> 1f3673266cbedaac1e44fae73e8bc03510da1625
                    break;
                case 's':
                    this.robot.setControllersActive(true);
                    break;
                case 'b':
                    this.robot.setControllersActive(true);
                    break;
                case 'h':
                case ' ':
                    this.robot.MOTION_CONTROLLER.setMode(MotionController.MotionMode.OFF);
                    ((Diag4)this.robot).SPINNERKICK_CONTROLLER.setActive(false);
                    ((Diag4)this.robot).SPINNERKICK_CONTROLLER.disengageSpinner(true);
                    ((Diag4)this.robot).SPINNERKICK_CONTROLLER.disengageSpinner(true);
                    ((Diag4)this.robot).SPINNERKICK_CONTROLLER.disengageSpinner(true);
                    break;
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        r.setText("");
    }
}
