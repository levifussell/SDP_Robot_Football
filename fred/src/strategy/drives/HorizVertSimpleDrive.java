package strategy.drives;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.interfaces.RobotPort;
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
    IN_FRONT_OF_BALL_ANGLE_INLINE,
    IN_FRONT_OF_BALL_ANGLE_OFFLINE,
    BEHIND_BALL_ANGLE_OFFLINE,
    BEHIND_BALL_ANGLE_INLINE,
    UNKNOWN
  }

  private BallTrackState ballTrackState = BallTrackState.UNKNOWN;
  private double actionTargetRadius = 0.0;
  private double actionTargetAngle = 0.0;

  private int MAX_MOTION = 100;

  private static final double SIZE_OF_ROBOT_IN_PIXELS = 20.0;

  private static final boolean DEBUG_MODE = true;

  //converts a cartesian point, given an origin, to polar coordinates
  public VectorGeometry toPolarCoord(VectorGeometry origin, VectorGeometry position)
  {
    VectorGeometry diff = new VectorGeometry(origin.x - position.x, origin.y - position.y);
    double radius = Math.sqrt(diff.x*diff.x + diff.y*diff.y);
    double angle = Math.atan2(diff.y, diff.x);

    return new VectorGeometry(radius, angle);
  }

  //polar coordinate origin calculator
  public double[] goToOriginPolarCoord(Robot us, VectorGeometry origin)
  {
    double[] powerVec = {0.0, 0.0, 0.0, 0.0};

    double ourAngle = us.location.direction;
    ourAngle = (Math.PI + Math.PI *2 + ourAngle) % (Math.PI * 2);

    VectorGeometry distToGoal = new VectorGeometry(origin.x - us.location.x, origin.y - us.location.y);

    double expectedAngle = (Math.atan2(distToGoal.y, distToGoal.x) + Math.PI) % (Math.PI * 2);
    double distToAngle = expectedAngle - ourAngle;
    System.out.println("EXP: " + expectedAngle);
    System.out.println("OUR: " + ourAngle);

    if (Math.abs(distToAngle) < Math.PI / 10.0) return powerVec;

    double k = 20.0 / Math.PI;
    double powerConst = Math.max(20.0, Math.abs(distToAngle) * k);
    double power = distToAngle < 0 ? -powerConst : powerConst;

    for(int i = 0; i < powerVec.length; ++i)
      powerVec[i] = power;

    return powerVec;
  }

  //polar coordinate angle calculator
  public double[] goToRadiusPolarCoord(Robot us, VectorGeometry origin, double targetRadius)
  {
    //polar coords: (radius, angle)
    VectorGeometry polarCoords = this.toPolarCoord(us.location, origin);

    if(DEBUG_MODE)
      System.out.println("POlAR COORDS: " + polarCoords.toString());

    double distToRadius = polarCoords.x - targetRadius;

    if(DEBUG_MODE)
      System.out.println("DIST TO ANGLE: " + distToRadius);

    double powerConst = Math.max(60.0, 60.0 * (Math.abs(distToRadius) / 120.0));
    double power = distToRadius < 0 ? powerConst : -powerConst;

    double[] powerVec = {0, 0, power, -power};

    return powerVec;
  }

  //polar coordinate angle calculator
  public double[] goToAnglePolarCoord(Robot us, VectorGeometry origin, double targetAngle)
  {
    //polar coords: (radius, angle)
    VectorGeometry polarCoords = this.toPolarCoord(us.location, origin);

    if(DEBUG_MODE)
      System.out.println("POlAR COORDS: " + polarCoords.toString());

    double distToAngle = polarCoords.y - targetAngle;

    if(DEBUG_MODE)
      System.out.println("DIST TO ANGLE: " + distToAngle);

    double powerConst = Math.max(60.0, 60.0 * (distToAngle / Math.PI));
    double power = distToAngle < 0 ? -powerConst : powerConst;

    //front wheel runs slower relative to the radius
    double wheelPowerFront =
        (polarCoords.x - SIZE_OF_ROBOT_IN_PIXELS / 2) / (polarCoords.x + SIZE_OF_ROBOT_IN_PIXELS / 2)
            * power;
    //back wheel runs faster relative to the radius
    double wheelPowerBack =
        (polarCoords.x + SIZE_OF_ROBOT_IN_PIXELS / 2) / (polarCoords.x - SIZE_OF_ROBOT_IN_PIXELS / 2)
            * power;
    double[] powerVec = {-wheelPowerFront, wheelPowerBack, 0.0, 0.0};

    return powerVec;
  }

  //merge all 3 drive dimensions
  public double[] performPolarCoordPowerCalc(
          double[] originPower, double[] radiusPower, double[] anglePower,  double angleForRotatePriority)
  {
    double[] totalPowerDrive = new double[4];

    for(int i = 0; i < 4; ++i) {
      // if not within 45 degrees of target only rotate
      if (Math.abs(angleForRotatePriority) > Math.PI / 2.0) {
        totalPowerDrive[i] = originPower[i];
      } else {
        totalPowerDrive[i] = (originPower[i] + radiusPower[i] + anglePower[i]) / 3.0;
      }

      if (totalPowerDrive[i] > 0) {
        totalPowerDrive[i] += 40;
      } else if (totalPowerDrive[i] < 0) {
        totalPowerDrive[i] -= 40;
      }
    }

    return totalPowerDrive;
  }

  public BallTrackState getBallTrackState(Robot us, VectorGeometry ballPoint, VectorGeometry origin)
  {
    //convert the robot and ball position to polar coords
    VectorGeometry robotPolarCoords = this.toPolarCoord(origin, us.location);
    VectorGeometry ballPolarCoords = this.toPolarCoord(origin, ballPoint);

    //TODO: instead of doing diff of angle and then diff of radius,
    //TODO:   treat these like cartesian points and update both in
    //TODO:   conjunction.

    //calculate difference between robot angle and ball angle
    double angleDiff = ballPolarCoords.y - robotPolarCoords.y;
    double angleDiffAbs = Math.abs(angleDiff);
    //calculate difference between robot radius and ball radius
    double radiusDiff = ballPolarCoords.x - robotPolarCoords.x;

    //CONSTANTS TODO---------------------
    final double radiusThreshold = 20.0;
    final double angleThreshold = Math.PI / 4;
    //-----------------------------------

    //stay where we are
    actionTargetRadius = robotPolarCoords.x;
    actionTargetAngle = robotPolarCoords.y;

    if(robotPolarCoords.x > ballPolarCoords.x + radiusThreshold)
    {
      if(angleDiffAbs < angleThreshold)
      {
        actionTargetRadius = ballPolarCoords.x;
        return BallTrackState.BEHIND_BALL_ANGLE_INLINE;
      }
      else
      {
        actionTargetAngle = ballPolarCoords.y;
        return BallTrackState.BEHIND_BALL_ANGLE_OFFLINE;
      }
    }
    else
    {
      if(angleDiffAbs < angleThreshold)
      {
        actionTargetAngle = ballPolarCoords.y + angleThreshold;
        return BallTrackState.IN_FRONT_OF_BALL_ANGLE_INLINE;
      }
      else
      {
        actionTargetRadius = ballPolarCoords.x + radiusThreshold * 2.0;
        return BallTrackState.IN_FRONT_OF_BALL_ANGLE_OFFLINE;
      }
    }

  }

  public double[] getActionBallTrackedState(Robot us, VectorGeometry origin)
  {
    double[] powerToGoal = this.goToOriginPolarCoord(us, origin);

    // HACK-------
    double rotOffset = 0;
    {
      double ourAngle = us.location.direction;
      ourAngle = (Math.PI + Math.PI * 2 + ourAngle) % (Math.PI * 2);

      VectorGeometry distToGoal = new VectorGeometry(origin.x - us.location.x, origin.y - us.location.y);

      double expectedAngle = (Math.atan2(distToGoal.y, distToGoal.x) + Math.PI) % (Math.PI * 2);
      rotOffset = expectedAngle - ourAngle;
    }
    // END OF HACK------

    //drive our robot towards the target radius from the origin (enemy goal)
    double targetRadius = actionTargetRadius;
    double[] powerToRadius = this.goToRadiusPolarCoord(us, origin, targetRadius);

    //drive our robot towards the target angle from the origin (enemy goal)
    double targetAngle = actionTargetAngle;
    double[] powerToAngle = this.goToAnglePolarCoord(us, origin, targetAngle);

    //merge all power motions of the robot
    double[] totalPowerDrive = performPolarCoordPowerCalc(powerToGoal, powerToRadius, powerToAngle, rotOffset);

    return totalPowerDrive;
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
      BallTrackState state = getBallTrackState(us, ballPoint, origin);
    //TODO: for now we don't use ball state but that's fine. Will fix this later
      double[] totalPowerDrive = getActionBallTrackedState(us, origin);
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
