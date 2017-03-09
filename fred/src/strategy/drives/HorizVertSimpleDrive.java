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
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

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

  private int MAX_MOTION = 100;

  private static final double SIZE_OF_ROBOT_IN_PIXELS = 20.0;

  private static final boolean DEBUG_MODE = true;

  private RobotPort commandPort;

  //converts a cartesian point, given an origin, to polar coordinates
  public VectorGeometry toPolarCoord(VectorGeometry origin, VectorGeometry position)
  {
	VectorGeometry diff = new VectorGeometry(origin.x - position.x, origin.y - position.y);
	double radius = Math.sqrt(diff.x*diff.x + diff.y*diff.y);
	double angle = (Math.PI * 2.5 + Math.atan2(diff.y, diff.x)) % (Math.PI * 2);

	return new VectorGeometry(radius, angle);
  }

  public String robotInPath(VectorGeometry[] robots, double ourRadius,
                            double ourAngle, double targetRadius, double targetAngle) {

	// check if robot is in the destination path of diag4 and where
	String whereIsTheRobot = "Nowhere";
	final double angleThreshold = Math.PI / 40;
	for(VectorGeometry robot : robots) {
      double targetAngleDiff = Math.abs(robot.y - targetAngle);
      double robotsAngleDiff = Math.abs(robot.y - ourAngle);
      if(targetAngleDiff <= angleThreshold && robotsAngleDiff <= angleThreshold) {
        if(robot.x < ourRadius  && robot.x >= targetRadius){
          whereIsTheRobot = "In front";
		} else if(robot.x < targetRadius){
          whereIsTheRobot = "Behind";
        }
	  } else if(targetAngleDiff <= angleThreshold || robotsAngleDiff <= angleThreshold) {
        if(robot.x >= targetRadius && robot.x < ourRadius) {
          whereIsTheRobot = "In front";
        }
      }
	}
	  return whereIsTheRobot;
  }

  public BallTrackState getBallTrackState(Robot us, VectorGeometry ballPoint, VectorGeometry origin)
  {
	// get other robots
	Robot friend = Strategy.world.getRobot(RobotType.FRIEND_1);
	Robot foe1 = Strategy.world.getRobot(RobotType.FOE_1);
	Robot foe2 = Strategy.world.getRobot(RobotType.FOE_2);

	//convert the robots and ball position to polar coords
	VectorGeometry diag4PolarCoords = this.toPolarCoord(origin, us.location);
	VectorGeometry friendPolarCoords = this.toPolarCoord(origin, friend.location);
	VectorGeometry foe1PolarCoords = this.toPolarCoord(origin, foe1.location);
	VectorGeometry foe2PolarCoords = this.toPolarCoord(origin, foe2.location);
	VectorGeometry ballPolarCoords = this.toPolarCoord(origin, ballPoint);

	VectorGeometry[] players = {friendPolarCoords, foe1PolarCoords, foe2PolarCoords};
	//TODO: instead of doing diff of angle and then diff of radius,
	//TODO:   treat these like cartesian points and update both in
	//TODO:   conjunction.

	//calculate difference between robot angle and ball angle
	double angleDiff = ballPolarCoords.y - diag4PolarCoords.y;
	double angleDiffAbs = Math.abs(angleDiff);
	//calculate difference between robot radius and ball radius
	double radiusDiff = ballPolarCoords.x - diag4PolarCoords.x;

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
			&& diag4PolarCoords.x - ballPolarCoords.x < 25
			&& diag4PolarCoords.x >= ballPolarCoords.x) {
	  ((Diag4RobotPort) commandPort).spamKick();
	}

	/* case A: path on a straight line; check if there is
	 a robot on it too and where
	*/
	if (angleDiffAbs < angleThreshold2 && diag4PolarCoords.x > ballPolarCoords.x) {
	  double targetRadius = ballPolarCoords.x - radiusOffset;
	  double targetAngle = ballPolarCoords.y;
	  // if robot in the middle of diag4's path move next to the ball
      if(robotInPath(players, diag4PolarCoords.x, diag4PolarCoords.y, targetRadius, targetAngle) == "In front") {
		System.out.println("GOING NEXT TO THE BALL");
		double actionTargetRadius = ballPolarCoords.x;
		double a = 2 * Math.asin(radiusThreshold / (2.0 * ballPolarCoords.x));
		double actionTargetAngle = ballPolarCoords.y + (ballPolarCoords.y > Math.PI / 2.0 ? -a : a);
		  polarNavigator.SetTargetState((float)actionTargetRadius, (float)actionTargetAngle);
		return BallTrackState.GO_NEXT_TO_BALL;
	  /* if robot in the middle of ball and enemy goal, block any passes by staying behind
         the ball */
      } else if(robotInPath(players, diag4PolarCoords.x, diag4PolarCoords.y, targetRadius, targetAngle) == "Behind") {
        System.out.println("GOING BEHIND BALL");
		  double actionTargetRadius = ballPolarCoords.x + radiusThreshold;
		  double actionTargetAngle = ballPolarCoords.y;
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
	} else if(diag4PolarCoords.x > ballPolarCoords.x) {
	  System.out.println("GOING BEHIND BALL");
		double actionTargetRadius = ballPolarCoords.x + radiusThreshold;
		double actionTargetAngle = ballPolarCoords.y;
		polarNavigator.SetTargetState((float)actionTargetRadius, (float)actionTargetAngle);
	  return BallTrackState.GO_BEHIND_BALL;
	} else if (diag4PolarCoords.x < ballPolarCoords.x) {
	  System.out.println("GOING NEXT TO THE BALL");
		double actionTargetRadius = ballPolarCoords.x;
	  double a = 2 * Math.asin(radiusThreshold / (2.0 * ballPolarCoords.x));
		double actionTargetAngle = ballPolarCoords.y + (ballPolarCoords.y > Math.PI / 2.0 ? -a : a);
		polarNavigator.SetTargetState((float)actionTargetRadius, (float)actionTargetAngle);
	  return BallTrackState.GO_NEXT_TO_BALL;
	} else {
	  System.out.println("UNKNOWN STATE");
		double actionTargetRadius = diag4PolarCoords.x;
		double actionTargetAngle = diag4PolarCoords.y;
		polarNavigator.SetTargetState((float)actionTargetRadius, (float)actionTargetAngle);
	  return BallTrackState.UNKNOWN;
	}

  }

  public double[] getActionBallTrackedState(Robot us, VectorGeometry origin, VectorGeometry ballPoint)
  {
	  CartesianCoordinate usCC = new CartesianCoordinate((float)us.location.x, (float)us.location.y);
	  CartesianCoordinate originCC = new CartesianCoordinate((float)origin.x, (float)origin.y);
	  PolarCoordinate usPC = PolarCoordinate.CartesianToPolar(usCC, originCC);

	  return this.polarNavigator.TransformDrive4Wheel(usPC, us.location.direction);
  }

  @Override
  //------------------------------------------------------------------------------
  //IMPORTANT NOTE: we don't give two shits about the parameters in this method. An important TODO is
  // to change this from the FRED codebase, etc.
  //------------------------------------------------------------------------------
  public void move(RobotPort port, DirectedPoint location, VectorGeometry force, double rotation, double factor) {
	assert(port instanceof FourWheelHolonomicRobotPort);

	//try to get our robot from the world
	Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
	if(us == null)
	  return;

	//our origin for the polar coordinates is the enemy goal always
	// (for now)
	EnemyGoal enem = new EnemyGoal();
	enem.recalculate();
	VectorGeometry origin = new VectorGeometry(enem.getX(), enem.getY());

	BallPoint ballP = new BallPoint();
	ballP.recalculate();
	VectorGeometry ballPoint = new VectorGeometry(ballP.getX(), ballP.getY());

	commandPort = port;

	//always keep our robot rotated towards the goal
//    double[] powerToGoal = this.goToOriginPolarCoord(us, origin);
//
//    // HACK-------
//    double rotOffset = 0;
//    {
//      double ourAngle = us.location.direction;
//      ourAngle = (Math.PI + Math.PI * 2 + ourAngle) % (Math.PI * 2);
//
//      VectorGeometry distToGoal = new VectorGeometry(origin.x - us.location.x, origin.y - us.location.y);
//
//      double expectedAngle = (Math.atan2(distToGoal.y, distToGoal.x) + Math.PI) % (Math.PI * 2);
//      rotOffset = expectedAngle - ourAngle;
//    }
//    // END OF HACK------
//
//    //drive our robot towards the target radius from the origin (enemy goal)
//    double targetRadius = 120.0; //TODO: this is just a random radius for testing (future note: move robot away from ball and move behind)
//    double[] powerToRadius = this.goToRadiusPolarCoord(us, origin, targetRadius);
//
//    //drive our robot towards the target angle from the origin (enemy goal)
//    double targetAngle = Math.PI / 2; //TODO: this is just a random angle for testing (future note: move robot away from ball and move behind)
//    double[] powerToAngle = this.goToAnglePolarCoord(us, origin, targetAngle);
//
//    //merge all power motions of the robot
//    double[] totalPowerDrive = performPolarCoordPowerCalc(powerToGoal, powerToRadius, powerToAngle, rotOffset);
//      this.ballTrackState = getBallTrackState(us, ballPoint, origin);
	//TODO: for now we don't use ball state but that's fine. Will fix this later
	  double[] totalPowerDrive = getActionBallTrackedState(us, origin, ballPoint);
//    for(int i = 0; i < 4; ++i) {
//      // if not within 45 degrees of target only rotate
//      if (Math.abs(rotOffset) > Math.PI / 2.0) {
//        totalPowerDrive[i] = powerToGoal[i];
//      } else {
//        totalPowerDrive[i] = (powerToGoal[i] + powerToRadius[i] + powerToAngle[i]) / 3.0;
//      }
//
//      if (totalPowerDrive[i] > 0) {
//        totalPowerDrive[i] += 40;
//      } else if (totalPowerDrive[i] < 0) {
//        totalPowerDrive[i] -= 40;
//      }
//    }

	//send drive to wheels
	((FourWheelHolonomicRobotPort) port).
		fourWheelHolonomicMotion(totalPowerDrive[0], totalPowerDrive[1], totalPowerDrive[2], totalPowerDrive[3]);
  }
}


