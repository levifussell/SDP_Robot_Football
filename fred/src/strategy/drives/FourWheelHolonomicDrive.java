package strategy.drives;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.interfaces.RobotPort;
import sun.nio.cs.ext.MacHebrew;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */

public class FourWheelHolonomicDrive implements DriveInterface{

    public int MAX_ROTATION = 30;
    public int MAX_MOTION = 100;

    public void move(RobotPort port, DirectedPoint location, VectorGeometry force, double rotation, double factor){
        assert(port instanceof FourWheelHolonomicRobotPort);

        VectorGeometry dir = new VectorGeometry();
        force.copyInto(dir).coordinateRotation(force.angle() - location.direction);
//        factor = Math.min(1, factor);
        // we do not care how close we get to the target,
        //  just go straight to it with no limits
        factor = 0;

        double lim = this.MAX_MOTION - Math.abs(rotation* this.MAX_ROTATION *factor);

        //rotate the Fred orientation by 45 degrees to create diagonal drive
        // (otherwise all remains the same)
        //dir = dir.rotate(Math.PI / 4.0f);

        double front = dir.y;
        double left = -dir.x;
        double back = -dir.y;
        double right = dir.x;
        //double front = -dir.x;
        //double left = dir.y;
        //double back = dir.x;
        //double right = -dir.y;
        double normalizer = Math.max(Math.max(Math.abs(left), Math.abs(right)), Math.max(Math.abs(front), Math.abs(back)));

        normalizer = lim/normalizer*factor;
        front = front*normalizer + rotation * this.MAX_ROTATION;
        back  = back*normalizer + rotation * this.MAX_ROTATION;
        left  = left*normalizer + rotation * this.MAX_ROTATION;
        right = right*normalizer + rotation * this.MAX_ROTATION;

        ((FourWheelHolonomicRobotPort) port).fourWheelHolonomicMotion(front, back, left, right);

    }
}
