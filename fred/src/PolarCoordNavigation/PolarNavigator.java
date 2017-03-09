package PolarCoordNavigation;

import PolarCoordNavigation.Coordinates.CartesianCoordinate;
import PolarCoordNavigation.Coordinates.PolarCoordinate;
import PolarCoordNavigation.DimensionNavigation.AngleDimensionNavigation;
import PolarCoordNavigation.DimensionNavigation.PointingToOriginDimensionNavigation;
import PolarCoordNavigation.DimensionNavigation.RadiusDimensionNavigation;

/**
 * Created by levif on 09/03/17.
 */
public class PolarNavigator {

    private static final double POWER_CONST_RADIUS = 60.0;
    private static final double POWER_CONST_ANGLE = 60.0;
    private static final double SIZE_OF_ROBOT_IN_PIXELS = 20.0;

    private PolarNavigationState targetState;
    private RadiusDimensionNavigation radiusDimensionNavigation;
    private AngleDimensionNavigation angleDimensionNavigation;
    private PointingToOriginDimensionNavigation pointingToOriginDimensionNavigation;

    public PolarNavigator(CartesianCoordinate origin)
    {
        this.targetState = new PolarNavigationState();
        this.targetState.UpdateState(origin, null);
    }

    /**
     * Given some object and it's angle, this will return the necessary
     * wheel drives to move the robot towards the state of the navigator
     * @param object
     * @param angleDirection
     */
    public double[] TransformDrive4Wheel(CartesianCoordinate object, double angleDirection)
    {
        PolarCoordinate objPolar = PolarCoordinate.CartesianToPolar(object, this.targetState.getOrigin());

        double[] driveToRadius =
                this.radiusDimensionNavigation.Drive4WheelToTarget(objPolar.getRadius(),
                        this.targetState.getObject().getRadius());

        double[] driveToAngle =
                this.angleDimensionNavigation.Drive4WheelToTarget(objPolar.getAngle(),
                        this.targetState.getObject().getAngle(), objPolar.getRadius());

        //angle preprocessing for facing origin
        double angleOriginDirection = (Math.PI + Math.PI *2 + angleDirection) % (Math.PI * 2);
        double targetOriginAngle = (this.targetState.getObject().getAngle() + Math.PI) % (Math.PI * 2);
        double[] driveToOriginPoint =
                this.pointingToOriginDimensionNavigation.Drive4WheelToTarget(angleOriginDirection,
                        targetOriginAngle);

        //create a combined vector for all the drive vectors
        double[] totalPowerDrive = new double[4];

        double rotOffset = targetOriginAngle - angleDirection;

        for(int i = 0; i < 4; ++i) {
            // if not within 45 degrees of target only rotate
            if (Math.abs(rotOffset) > Math.PI / 2.0) {
                totalPowerDrive[i] = driveToOriginPoint[i];
            } else {
                totalPowerDrive[i] = (driveToOriginPoint[i] + driveToRadius[i] + driveToAngle[i]) / 3.0;
            }

            if (totalPowerDrive[i] > 0) {
                totalPowerDrive[i] += 40;
            } else if (totalPowerDrive[i] < 0) {
                totalPowerDrive[i] -= 40;
            }
        }

        return totalPowerDrive;
    }

    /**
     * Created by levif on 09/03/17.
     *
     * This class represents the state of some polar coordinate world
     * and an object within it
     */
    public static class PolarNavigationState {

        private CartesianCoordinate origin;
        private PolarCoordinate object;

        public PolarNavigationState()
        {
            this.origin = new CartesianCoordinate();
            this.object = new PolarCoordinate();
        }

        public void UpdateState(CartesianCoordinate origin, PolarCoordinate object)
        {
            if(origin != null)
            {
                this.origin = origin;
            }

            if(object != null)
            {
                this.object = object;
            }
        }

        public PolarCoordinate getObject() { return this.object; }
        public CartesianCoordinate getOrigin() { return this.origin; }
    }
}
