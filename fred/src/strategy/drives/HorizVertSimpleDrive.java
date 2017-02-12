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

  @Override
  public void move(RobotPort port, DirectedPoint location, VectorGeometry force, double rotation, double factor) {
    assert(port instanceof FourWheelHolonomicRobotPort);
    //location => where we want the robot to go (x, y)
    //force => where the robot currently is (x, y)

//    VectorGeometry distTo = new VectorGeometry(location.x - force.x, location.y - force.y);
//
//    double fieldSize = 100.0f;
//    double dimX = MAX_MOTION * (distTo.x / fieldSize);
//    double dimY = MAX_MOTION * (distTo.y / fieldSize);
//
//    double front = -dimX;
//    double left = -dimY;
//    double back = dimX;
//    double right = dimY;
    EnemyGoal enem = new EnemyGoal();
    enem.recalculate();

    Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
    if(us == null)
      return;

    double ourAngle = us.location.direction;
    ourAngle = (Math.PI + Math.PI *2 + ourAngle) % (Math.PI * 2);

    VectorGeometry distToGoal = new VectorGeometry(enem.getX() - us.location.x, enem.getY() - us.location.y);

    double expectedAngle = (Math.atan2(distToGoal.y, distToGoal.x) + Math.PI) % (Math.PI * 2);
    double distToAngle = expectedAngle - ourAngle;
    System.out.println("EXP: " + expectedAngle);
    System.out.println("OUR: " + ourAngle);

    if (Math.abs(distToAngle) < Math.PI / 30.0) return;

    double k = 15.0 / Math.PI;
    double powerConst = 25 + Math.abs(distToAngle) * k;
    double power = distToAngle < 0 ? -powerConst : powerConst;

    ((FourWheelHolonomicRobotPort) port).fourWheelHolonomicMotion(power, power, power, power);

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
