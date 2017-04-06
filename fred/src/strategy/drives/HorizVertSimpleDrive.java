package strategy.drives;

import PolarCoordNavigation.Coordinates.PolarCoordinate;
import PolarCoordNavigation.PolarNavigator;
import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.interfaces.RobotPort;
import communication.ports.robotPorts.Diag4RobotPort;
import strategy.GUI;
import strategy.Strategy;
import strategy.points.ImportantPoints;
import strategy.states.*;
import strategy.states.StateManager;
import vision.Robot;
import vision.RobotType;

/**
 * Created by levi on 04/02/17.
 */
public class HorizVertSimpleDrive implements DriveInterface {

    //TODO: this can just be represented by two booleans. For now we do this
    //TODO:   to make it clear what we are trying to do
    public enum BallTrackState {
        GO_BEHIND_BALL,
        GO_NEXT_TO_BALL,
        GO_TO_BALL,
        UNKNOWN
    }

    public void initializeStandard() {
        StateManager.addState(new GoingToBallState());
        StateManager.addState(new BehindBallState());
        StateManager.addState(new NextToBallState());
        StateManager.addState(new StallState());
    }

    public void initializeAvoidance() {
        StateManager.addState(new GTBStateAvoidance());
        StateManager.addState(new HoverBRStateAvoidance());
        StateManager.addState(new BBStateAvoidance());
        StateManager.addState(new NTBStateAvoidance());
    }

    PolarNavigator polarNavigator = new PolarNavigator();

    private static final boolean DEBUG_MODE = true;

    private boolean init = false;
    private boolean avoidance = false;

    private RobotPort commandPort;


    public BallTrackState getBallTrackState() {
        //convert the robots and ball position to polar coords
        PolarCoordinate diag4PolarCoords = ImportantPoints.getRobotPolar(RobotType.FRIEND_2);
        PolarCoordinate friendPolarCoords = ImportantPoints.getRobotPolar(RobotType.FRIEND_1);
        PolarCoordinate foe1PolarCoords = ImportantPoints.getRobotPolar(RobotType.FOE_1);
        PolarCoordinate foe2PolarCoords = ImportantPoints.getRobotPolar(RobotType.FOE_2);
        PolarCoordinate ballPolarCoords = ImportantPoints.getBallPolar();

        PolarCoordinate[] players = {friendPolarCoords, foe1PolarCoords, foe2PolarCoords};
        //TODO: instead of doing diff of angle and then diff of radius,
        //TODO:   treat these like cartesian points and update both in
        //TODO:   conjunction.

        //calculate difference between robot angle and ball angle
        double angleDiff = ballPolarCoords.getAngle() - diag4PolarCoords.getAngle();
        double angleDiffAbs = Math.abs(angleDiff);
        //calculate difference between robot radius and ball radius
        double radiusDiff = ballPolarCoords.getRadius() - diag4PolarCoords.getRadius();

//    System.out.println("DIFF ANGLE: " + angleDiff);
//    System.out.println("DIFF RADIUS: " + radiusDiff);

        //CONSTANTS TODO---------------------
        final double radiusThreshold = 30.0;
        final double radiusOffset = 0.0;
//    final double angleThreshold = Math.PI / 10;
        final double angleThreshold2 = Math.PI / 40;
        //-----------------------------------

        //FOR TESTING IF THE ROBOT GOES TO THE BALL---
//	  PolarCoordinate ballTarget = ImportantPoints.getBallPolar();
//	  polarNavigator.SetTargetState(ballTarget.getRadius(), ballTarget.getAngle());
//	  if(ballTarget != null)
//	  	return BallTrackState.UNKNOWN;


        //kicking
        if (angleDiffAbs <= angleThreshold2
                && diag4PolarCoords.getRadius() - ballPolarCoords.getRadius() < 25
                && diag4PolarCoords.getRadius() >= ballPolarCoords.getRadius()) {
            ((Diag4RobotPort) commandPort).spamKick();
        }

        State currentState = StateManager.getCurrentState();

        double actionTargetRadius = currentState.getRadius();
        double actionTargetAngle = currentState.getAngle();

        System.out.println("R: " + String.valueOf(actionTargetRadius) + " A: " + String.valueOf(actionTargetAngle));

        polarNavigator.SetTargetState((float) actionTargetRadius, (float) actionTargetAngle);

        return currentState.getBallTrack();

    }


    public double[] getActionBallTrackedState(double usAngleDirection) {
        getBallTrackState();

        return this.polarNavigator.TransformDrive4Wheel(
                ImportantPoints.getRobotPolar(RobotType.FRIEND_2), usAngleDirection);
    }

    @Override
    //------------------------------------------------------------------------------
    //IMPORTANT NOTE: we don't give two shits about the parameters in this method. An important TODO is
    // to change this from the FRED codebase, etc.
    //------------------------------------------------------------------------------
    public void move(RobotPort port) {
        assert (port instanceof FourWheelHolonomicRobotPort);

        if (!init) {
            if (GUI.initialise.equals("Avoidance")) {
                initializeAvoidance();
            } else {
                initializeStandard();
            }
            init = true;
        }

        StateManager.update();

        //try to get our robot from the world
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if (us == null) {
            return;
        }

        //FIRST WE UPDATE OUR ORIGIN OF POLAR COORDS TO THE ENEMY GOAL
        // (just in case for some reason this has changed)
        ImportantPoints.setOrigin(ImportantPoints.getEnemyGoalCartesian());

        commandPort = port;

        double[] totalPowerDrive = getActionBallTrackedState(us.location.direction);

        //send drive to wheels
        ((FourWheelHolonomicRobotPort) port).
                fourWheelHolonomicMotion(totalPowerDrive[0], totalPowerDrive[1], totalPowerDrive[2], totalPowerDrive[3]);
    }
}


