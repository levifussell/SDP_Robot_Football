package strategy.drives;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.interfaces.RobotPort;
import strategy.Strategy;
import strategy.points.basicPoints.EnemyGoal;
import vision.Robot;
import vision.RobotType;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

/**
 * Created by levi on 04/02/17.
 */
public class HorizVertSimpleDrive implements DriveInterface {

  private int MAX_MOTION = 100;

  private static final double SIZE_OF_ROBOT_IN_PIXELS = 20.0;

  private static final boolean DEBUG_MODE = true;

  public VectorGeometry toPolarCoord(VectorGeometry origin, VectorGeometry position)
  {
    VectorGeometry diff = new VectorGeometry(origin.x - position.x, origin.y - position.y);
    double radius = Math.sqrt(diff.x*diff.x + diff.y*diff.y);
    double angle = Math.atan2(diff.y, diff.x);

    return new VectorGeometry(radius, angle);
  }

  public double[] calcPowerToRotateToOrigin(Robot us, VectorGeometry origin)
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

  public double[] goToRadius(Robot us, VectorGeometry origin, double targetRadius)
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

  public double[] goToAngle(Robot us, VectorGeometry origin, double targetAngle)
  {
    //polar coords: (radius, angle)
    VectorGeometry polarCoords = this.toPolarCoord(us.location, origin);

    if(DEBUG_MODE)
      System.out.println("POlAR COORDS: " + polarCoords.toString());

    double distToAngle = polarCoords.y - targetAngle;

    if(DEBUG_MODE)
      System.out.println("DIST TO ANGLE: " + distToAngle);

    double powerConst = Math.max(100.0, 100.0 * (distToAngle / Math.PI));
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

  @Override
  public void move(RobotPort port, DirectedPoint location, VectorGeometry force, double rotation, double factor) {
    assert(port instanceof FourWheelHolonomicRobotPort);
    //location => where we want the robot to go (x, y)
    //force => where the robot currently is (x, y)

//    VectorGeometry distTo = new VectorGeometry(location.x - force.x, location.y - force.y);
//
//    double fieldSize = 100.0f;wheelPowerFront
//    double dimX = MAX_MOTION * (distTo.x / fieldSize);
//    double dimY = MAX_MOTION * (distTo.y / fieldSize);
//
//    double front = -dimX;
//    double left = -dimY;
//    double back = dimX;
//    double right = dimY;

    //try to get our robot from the world
    Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
    if(us == null)
      return;

    //our origin for the polar coordinates is the enemy goal always
    // (for now)
    EnemyGoal enem = new EnemyGoal();
    enem.recalculate();
    VectorGeometry origin = new VectorGeometry(enem.getX(), enem.getY());

    //always keep our robot rotated towards the goal
    double[] powerToGoal = this.calcPowerToRotateToOrigin(us, origin);

    //drive our robot towards the target radius from the origin (enemy goal)
    double targetRadius = 120.0; //TODO: this is just a random radius for testing (future note: move robot away from ball and move behind)
    double[] powerToRadius = this.goToRadius(us, origin, targetRadius);

    //drive our robot towards the target angle from the origin (enemy goal)
    double targetAngle = Math.PI / 2; //TODO: this is just a random angle for testing (future note: move robot away from ball and move behind)
    double[] powerToAngle = this.goToAngle(us, origin, targetAngle);

    //sum all the powers (?)
    double[] totalPowerDrive = new double[4];
    for(int i = 0; i < 4; ++i) {
      totalPowerDrive[i] = powerToAngle[i]; //(powerToGoal[i] + powerToRadius[i]) / 2.0; //+ powerToAngle[i];
      /*if (totalPowerDrive[i] > 0) {
        totalPowerDrive[i] += 40;
      } else if (totalPowerDrive[i] < 0) {
        totalPowerDrive[i] -= 40;
      }*/
    }

    //send drive to wheels
    ((FourWheelHolonomicRobotPort) port).
        fourWheelHolonomicMotion(totalPowerDrive[0], totalPowerDrive[1], totalPowerDrive[2], totalPowerDrive[3]);

    //rotate counterclockwise

//    //move left
//    if(distTo.x < 0)
//    {
//      front = -MAX_MOTION;
//      back = MAX_MOTION;
//    }
//    //move right
//    else if(distTo.x > 0)
//    {
//      front = MAX_MOTION;
//      back = -MAX_MOTION;
//    }
//
//    //move forward
//    if(distTo.y < 0)
//    {
//      left = -MAX_MOTION;
//      right = MAX_MOTION;
//    }
//    //move backward
//    else if(distTo.y > 0)
//    {
//      left = MAX_MOTION;
//      right = -MAX_MOTION;
//    }

//    ((FourWheelHolonomicRobotPort) port).fourWheelHolonomicMotion(front, back, left, right);
  }
}
