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

    private PolarNavigationState targetState;
    private RadiusDimensionNavigation radiusDimensionNavigation;
    private AngleDimensionNavigation angleDimensionNavigation;
    private PointingToOriginDimensionNavigation pointingToOriginDimensionNavigation;

    public PolarNavigator(CartesianCoordinate origin)
    {
        this.targetState = new PolarNavigationState();
        this.targetState.UpdateState(origin, null);

        this.radiusDimensionNavigation = new RadiusDimensionNavigation();
        this.angleDimensionNavigation = new AngleDimensionNavigation();
        this.pointingToOriginDimensionNavigation = new PointingToOriginDimensionNavigation();
    }

    /**
     * Below methods update the state of the target that the navigator is trying to
     * reach
     */
    public void SetTargetRadius(float radius)
    {
        this.SetTargetState(radius, this.targetState.getObject().getAngle());
    }
    public void SetTargetAngle(float angle)
    {
        this.SetTargetState(this.targetState.getObject().getRadius(), angle);
    }
    public void SetTargetState(PolarCoordinate targetCoord)
    {
        this.targetState.UpdateState(null, targetCoord);
    }
    public void SetTargetState(float radius, float angle)
    {
        this.SetTargetState(new PolarCoordinate(radius, angle));
    }

    /**
     * Given some object and it's angle, this will return the necessary
     * wheel drives to move the robot towards the state of the navigator
     * @param object
     * @param angleDirection
     */
    public double[] TransformDrive4Wheel(PolarCoordinate object, double angleDirection)
    {
        double[] driveToRadius =
                this.radiusDimensionNavigation.Drive4WheelToTarget(object.getRadius(),
                        this.targetState.getObject().getRadius());

        double[] driveToAngle =
                this.angleDimensionNavigation.Drive4WheelToTarget(object.getAngle(),
                        this.targetState.getObject().getAngle(), object.getRadius());

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
}
