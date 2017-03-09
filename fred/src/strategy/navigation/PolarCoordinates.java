package strategy.navigation;

import vision.Robot;
import vision.tools.VectorGeometry;
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
 * Created by Luca Mc
 */
public class PolarCoordinates {

    private static final boolean DEBUG_MODE = true;

    public PolarCoordinates(){

    }

    //converts a cartesian point, given an origin, to polar coordinates
    public VectorGeometry toPolarCoord(VectorGeometry origin, VectorGeometry position)
    {
        VectorGeometry diff = new VectorGeometry(origin.x - position.x, origin.y - position.y);
        double radius = Math.sqrt(diff.x*diff.x + diff.y*diff.y);
        double angle = (Math.PI * 2.5 + Math.atan2(diff.y, diff.x)) % (Math.PI * 2);

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
        //System.out.println("EXP: " + expectedAngle);
        //System.out.println("OUR: " + ourAngle);

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
        VectorGeometry polarCoords = this.toPolarCoord(origin, us.location);

        if(DEBUG_MODE)
            System.out.println("POlAR COORDS: " + polarCoords.toString());

        double distToRadius = polarCoords.x - targetRadius;

        if(DEBUG_MODE)
            System.out.println("DIST TO ANGLE: " + distToRadius);

        //60.0
        double powerConst = Math.max(60.0, 60.0 * (Math.abs(distToRadius) / 120.0));
        double power = distToRadius < 0 ? powerConst : -powerConst;

        double[] powerVec = {0, 0, power, -power};

        return powerVec;
    }

    //polar coordinate angle calculator
    public double[] goToAnglePolarCoord(Robot us, VectorGeometry origin, double targetAngle)
    {
        //polar coords: (radius, angle)
        VectorGeometry polarCoords = this.toPolarCoord(origin, us.location);

        if(DEBUG_MODE)
            System.out.println("POlAR COORDS: " + polarCoords.toString());

        double distToAngle = polarCoords.y - targetAngle;

        if(DEBUG_MODE)
            System.out.println("DIST TO ANGLE: " + distToAngle);

        //60.0
        double powerConst = Math.max(60.0, 60.0 * (distToAngle / Math.PI));
        double power = distToAngle < 0 ? powerConst : -powerConst;

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
}
