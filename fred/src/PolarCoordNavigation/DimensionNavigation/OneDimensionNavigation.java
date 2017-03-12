package PolarCoordNavigation.DimensionNavigation;

/**
 * Created by s1408726 on 09/03/17.
 */
public abstract class OneDimensionNavigation {

    private double DirectionalisePower(double power, double polarity)
    {
        return polarity < 0 ? power : -power;
    }

    private double DistToTarget(double current, double target)
    {
        return current - target;
    }

    protected double DirectionalPowerToTarget(double current, double target)
    {
        double distTo = this.DistToTarget(current, target);

        double powerConst = this.PowerProcessing(distTo);

        return DirectionalisePower(powerConst, distTo);
    }

    protected abstract double PowerProcessing(double dist);

    public abstract double[] Drive4WheelToTarget(double current, double target, double... extraConsts);

}
