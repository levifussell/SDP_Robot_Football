package strategy.drives;

import PolarCoordNavigation.Coordinates.CartesianCoordinate;
import PolarCoordNavigation.Coordinates.PolarCoordinate;
import PolarCoordNavigation.PolarNavigator;
import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.interfaces.RobotPort;
import communication.ports.robotPorts.Diag4RobotPort;
import strategy.Strategy;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.EnemyGoal;
import vision.Robot;
import vision.RobotType;

/**
 * Created by levi on 04/02/17.
 */
public class HorizVertSimpleDrive implements DriveInterface {

  //TODO: this can just be represented by two booleans. For now we do this
  //TODO:   to make it clear what we are trying to do
  public enum BallTrackState
  {
	GO_BEHIND_BALL,
	GO_NEXT_TO_BALL,
	GO_TO_BALL,
	UNKNOWN
  }

	PolarNavigator polarNavigator;

  private static final boolean DEBUG_MODE = true;

  private RobotPort commandPort;

  public String robotInPath(PolarCoordinate[] robots, double ourRadius,
                            double ourAngle, double targetRadius, double targetAngle) {

	// check if robot is in the destination path of diag4 and where
	String whereIsTheRobot = "Nowhere";
	final double angleThreshold = Math.PI / 40;
	for(PolarCoordinate robot : robots) {
      double targetAngleDiff = Math.abs(robot.getAngle() - targetAngle);
      double robotsAngleDiff = Math.abs(robot.getAngle() - ourAngle);
      if(targetAngleDiff <= angleThreshold && robotsAngleDiff <= angleThreshold) {
        if(robot.getRadius() < ourRadius  && robot.getRadius() >= targetRadius){
          whereIsTheRobot = "In front";
		} else if(robot.getRadius() < targetRadius){
          whereIsTheRobot = "Behind";
        }
	  } else if(targetAngleDiff <= angleThreshold || robotsAngleDiff <= angleThreshold) {
        if(robot.getRadius() >= targetRadius && robot.getRadius() < ourRadius) {
          whereIsTheRobot = "In front";
        }
      }
	}
	  return whereIsTheRobot;
  }

  public BallTrackState getBallTrackState(CartesianCoordinate usPosition, CartesianCoordinate ballPoint, CartesianCoordinate origin)
  {
	// get other robots
	Robot friend = Strategy.world.getRobot(RobotType.FRIEND_1);
	  CartesianCoordinate friendCC = new CartesianCoordinate((float)friend.location.x, (float)friend.location.y);
	Robot foe1 = Strategy.world.getRobot(RobotType.FOE_1);
	  CartesianCoordinate foe1CC = new CartesianCoordinate((float)foe1.location.x, (float)foe1.location.y);
	Robot foe2 = Strategy.world.getRobot(RobotType.FOE_2);
	  CartesianCoordinate foe2CC = new CartesianCoordinate((float)foe2.location.x, (float)foe2.location.y);

	//convert the robots and ball position to polar coords
	  PolarCoordinate diag4PolarCoords = PolarCoordinate.CartesianToPolar(origin, usPosition);
	  PolarCoordinate friendPolarCoords = PolarCoordinate.CartesianToPolar(origin, friendCC);
	  PolarCoordinate foe1PolarCoords = PolarCoordinate.CartesianToPolar(origin, foe1CC);
	  PolarCoordinate foe2PolarCoords = PolarCoordinate.CartesianToPolar(origin, foe2CC);
	  PolarCoordinate ballPolarCoords = PolarCoordinate.CartesianToPolar(origin, ballPoint);

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

	//kicking
	if (angleDiffAbs <= angleThreshold2
			&& diag4PolarCoords.getRadius() - ballPolarCoords.getRadius() < 25
			&& diag4PolarCoords.getRadius() >= ballPolarCoords.getRadius()) {
	  ((Diag4RobotPort) commandPort).spamKick();
	}

	/* case A: path on a straight line; check if there is
	 a robot on it too and where
	*/
	if (angleDiffAbs < angleThreshold2 && diag4PolarCoords.getRadius() > ballPolarCoords.getRadius()) {
	  double targetRadius = ballPolarCoords.getRadius() - radiusOffset;
	  double targetAngle = ballPolarCoords.getAngle();
	  // if robot in the middle of diag4's path move next to the ball
      if(robotInPath(players, diag4PolarCoords.getRadius(), diag4PolarCoords.getAngle(), targetRadius, targetAngle) == "In front") {
		System.out.println("GOING NEXT TO THE BALL");
		double actionTargetRadius = ballPolarCoords.getRadius();
		double a = 2 * Math.asin(radiusThreshold / (2.0 * ballPolarCoords.getRadius()));
		double actionTargetAngle = ballPolarCoords.getAngle() + (ballPolarCoords.getAngle() > Math.PI / 2.0 ? -a : a);
		  polarNavigator.SetTargetState((float)actionTargetRadius, (float)actionTargetAngle);
		return BallTrackState.GO_NEXT_TO_BALL;
	  /* if robot in the middle of ball and enemy goal, block any passes by staying behind
         the ball */
      } else if(robotInPath(players, diag4PolarCoords.getRadius(), diag4PolarCoords.getAngle(), targetRadius, targetAngle) == "Behind") {
        System.out.println("GOING BEHIND BALL");
		  double actionTargetRadius = ballPolarCoords.getRadius() + radiusThreshold;
		  double actionTargetAngle = ballPolarCoords.getAngle();
		  polarNavigator.SetTargetState((float)actionTargetRadius, (float)actionTargetAngle);
        return BallTrackState.GO_BEHIND_BALL;
	  // if we reach this stage path is clear
      } else {
		System.out.println("GOING TO THE BALL");
		  double actionTargetRadius = targetRadius;
		  double actionTargetAngle = targetAngle;
		  polarNavigator.SetTargetState((float)actionTargetRadius, (float)actionTargetAngle);
		return BallTrackState.GO_TO_BALL;
	  }
    /* case B: */
	} else if(diag4PolarCoords.getRadius() > ballPolarCoords.getRadius()) {
	  System.out.println("GOING BEHIND BALL");
		double actionTargetRadius = ballPolarCoords.getRadius() + radiusThreshold;
		double actionTargetAngle = ballPolarCoords.getAngle();
		polarNavigator.SetTargetState((float)actionTargetRadius, (float)actionTargetAngle);
	  return BallTrackState.GO_BEHIND_BALL;
	} else if (diag4PolarCoords.getRadius() < ballPolarCoords.getRadius()) {
	  System.out.println("GOING NEXT TO THE BALL");
		double actionTargetRadius = ballPolarCoords.getRadius();
	  double a = 2 * Math.asin(radiusThreshold / (2.0 * ballPolarCoords.getRadius()));
		double actionTargetAngle = ballPolarCoords.getAngle() + (ballPolarCoords.getAngle() > Math.PI / 2.0 ? -a : a);
		polarNavigator.SetTargetState((float)actionTargetRadius, (float)actionTargetAngle);
	  return BallTrackState.GO_NEXT_TO_BALL;
	} else {
	  System.out.println("UNKNOWN STATE");
		double actionTargetRadius = diag4PolarCoords.getRadius();
		double actionTargetAngle = diag4PolarCoords.getAngle();
		polarNavigator.SetTargetState((float)actionTargetRadius, (float)actionTargetAngle);
	  return BallTrackState.UNKNOWN;
	}

  }

  public double[] getActionBallTrackedState(CartesianCoordinate usPosition, double usAngleDirection, CartesianCoordinate origin, CartesianCoordinate ballPoint)
  {
	  getBallTrackState(usPosition, ballPoint, origin);

	  PolarCoordinate usPC = PolarCoordinate.CartesianToPolar(usPosition, origin);

	  return this.polarNavigator.TransformDrive4Wheel(usPC, usAngleDirection);
  }

  @Override
  //------------------------------------------------------------------------------
  //IMPORTANT NOTE: we don't give two shits about the parameters in this method. An important TODO is
  // to change this from the FRED codebase, etc.
  //------------------------------------------------------------------------------
  public void move(RobotPort port) {
	assert(port instanceof FourWheelHolonomicRobotPort);

	//try to get our robot from the world
	Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
	if(us == null)
	  return;

	  CartesianCoordinate usPosition = new CartesianCoordinate((float)us.location.x, (float)us.location.y);

	//our origin for the polar coordinates is the enemy goal always
	// (for now)
	EnemyGoal enem = new EnemyGoal();
	enem.recalculate();
	  CartesianCoordinate origin = new CartesianCoordinate((float)enem.getX(), (float)enem.getY());

	BallPoint ballP = new BallPoint();
	ballP.recalculate();
	  CartesianCoordinate ballPoint = new CartesianCoordinate((float)ballP.getX(), (float)ballP.getY());

	commandPort = port;

	  double[] totalPowerDrive = getActionBallTrackedState(usPosition, us.location.direction, origin, ballPoint);

	//send drive to wheels
	((FourWheelHolonomicRobotPort) port).
		fourWheelHolonomicMotion(totalPowerDrive[0], totalPowerDrive[1], totalPowerDrive[2], totalPowerDrive[3]);
  }
}


