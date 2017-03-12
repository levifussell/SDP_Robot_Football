package strategy;

import communication.PortListener;
import communication.ports.robotPorts.Diag4RobotPort;
import strategy.points.basicPoints.*;
import strategy.robots.Diag4;
import strategy.robots.RobotBase;
import vision.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Simon Rovder
 */
public class Strategy implements VisionListener, PortListener, ActionListener {



    private Timer timer;
    private String action;
    private Vision vision;


    /**
     * SDP2017NOTE
     * The following variable is a static variable always containing the very last known state of the world.
     * It is accessible from anywhere in the project at any time as Strategy.world
     */
    public static DynamicWorld world = null;

    public static Status status;

    private String readLine() {
        try {
            return new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private RobotBase [] robots;

    public Strategy(String [] args) {

        /*
         * SDP2017NOTE
         * Create your robots in the following line. All these robots will be instantly connected to the
         * navigation system and all its controllers will be launched every cycle.
         */
        this.robots = new RobotBase [] {new Diag4(RobotType.FRIEND_2)};

//        Fred fred = (Fred) this.robots[0];
//        FredRobotPort port = (FredRobotPort) fred.port;
        Diag4 diag4 = (Diag4)this.robots[0];
        Diag4RobotPort port = (Diag4RobotPort)diag4.port;

        final Strategy semiStrategy = this;
        semiStrategy.vision = new Vision(args);
        semiStrategy.vision.addVisionListener(semiStrategy);


//        fred.PROPELLER_CONTROLLER.setActive(false);

        this.action = "";
        GUI.gui.doesNothingButIsNecessarySoDontDelete();
//        GUI.gui.setRobot(fred);
        GUI.gui.setRobot(diag4);
        this.timer = new Timer(100, this);
        this.timer.start();


        while(true){
            /*
             * SDP2017NOTE
             * This is a debug loop. You can add manual control over the robots here so as to make testing easier.
             * It simply loops forever. Vision System and Strategy run concurrently.
             *
             */
            System.out.print(">> ");
            this.action = this.readLine();
            if(this.action.equals("exit")){
                diag4.SPINNERKICK_CONTROLLER.disengageSpinner(true);
                diag4.SPINNERKICK_CONTROLLER.disengageSpinner(true);
                diag4.SPINNERKICK_CONTROLLER.disengageSpinner(true);
//                port.spinnerKick(0, 0);
//                port.spinnerKick(0, 0);
//                port.spinnerKick(0, 0);
                break;
            }
            switch(this.action){
                case "a":
                    diag4.setControllersActive(true);
                    break;
                case "!":
                    System.out.print(" Motion: ");
                    System.out.print(diag4.MOTION_CONTROLLER.isActive());
                    System.out.print(" Propeller: ");
                    System.out.println(diag4.SPINNERKICK_CONTROLLER.isActive());
                    break;
                case "h":
                    diag4.MOTION_CONTROLLER.setDestination(null);
                    diag4.MOTION_CONTROLLER.setHeading(null);
                    port.halt();
                    port.halt();
                    port.halt();
                    diag4.SPINNERKICK_CONTROLLER.setActive(false);
                    diag4.SPINNERKICK_CONTROLLER.disengageSpinner(true);
                    diag4.SPINNERKICK_CONTROLLER.disengageSpinner(true);
                    diag4.SPINNERKICK_CONTROLLER.disengageSpinner(true);
                    break;
                case "annoy":
                    diag4.MOTION_CONTROLLER.setDestination(new InFrontOfRobot(RobotAlias.FELIX));
                    diag4.MOTION_CONTROLLER.setHeading(new RobotPoint(RobotAlias.FELIX));
                    break;
                case "rot":
                    diag4.SPINNERKICK_CONTROLLER.setActive(false);
                    diag4.SPINNERKICK_CONTROLLER.disengageSpinner(true);
                    diag4.SPINNERKICK_CONTROLLER.disengageSpinner(true);
                    diag4.SPINNERKICK_CONTROLLER.disengageSpinner(true);
                    diag4.MOTION_CONTROLLER.setDestination(new Rotate());
                    diag4.MOTION_CONTROLLER.setHeading(new BallPoint());
                    break;
                case "p":
                    boolean act = diag4.SPINNERKICK_CONTROLLER.isActive();
                    diag4.SPINNERKICK_CONTROLLER.setActive(!act);
                    if(!act){
                        diag4.SPINNERKICK_CONTROLLER.disengageSpinner(true);
                    }
                    System.out.println(diag4.SPINNERKICK_CONTROLLER.isActive());
                    break;
                case "test":
                    diag4.MOTION_CONTROLLER.setHeading(new EnemyGoal());
                    diag4.MOTION_CONTROLLER.setDestination(new EnemyGoal());
                    break;
            }
        }

        this.vision.terminateVision();
        System.exit(0);
    }




    @Override
    public void nextWorld(DynamicWorld dynamicWorld) {
        world = dynamicWorld;
        status = new Status(world);
    }


    /**
     * SDP2017NOTE
     * This is the main() you want to run. It launches everything.
     * @param args
     */
    public static void main(String[] args) {
        new Strategy(args);
    }


    /**
     * SDP2017NOTE
     * This is the main loop of the entire strategy module. It is launched every couple of milliseconds.
     * Insert all your clever things here. You can access Strategy.world from here and control robots.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(world != null){
            for(RobotBase robot : this.robots){
                if(world.getRobot(robot.robotType) == null){
                    // Angry yelling.
                    Toolkit.getDefaultToolkit().beep();
                }
                try{
                    // Tells all the Controllers of each robot to do what they need to do.
                    robot.perform();
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void receivedStringHandler(String string) {

    }
}
