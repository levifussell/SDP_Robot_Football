package strategy.drives;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.interfaces.RobotPort;
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

    VectorGeometry distTo = new VectorGeometry(location.x - force.x, location.y - force.y);

    double fieldSize = 400.0f;
    double dimX = MAX_MOTION * (distTo.x / fieldSize);
    double dimY = MAX_MOTION * (distTo.y / fieldSize);

    double front = dimX;
    double left = -dimY;
    double back = -dimX;
    double right = dimY;

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

    ((FourWheelHolonomicRobotPort) port).fourWheelHolonomicMotion(front, back, left, right);
  }
}
