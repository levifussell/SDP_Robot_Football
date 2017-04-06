package strategy.states;

import strategy.drives.HorizVertSimpleDrive;
import strategy.points.ImportantPoints;
import vision.RobotType;

/**
 * Created by Luca & Levi
 */
public class NextToBallState extends State {

    @Override
    public String getName() { return "NextToBall"; }
    
    @Override
    public double getRadius() {
        return ImportantPoints.getBallPolar().getRadius();
    }

    @Override
    public double getAngle() {
        double a = 2 * Math.asin(30 / (2.0 * ImportantPoints.getBallPolar().getRadius()));
        double actionTargetAngle = ImportantPoints.getBallPolar().getAngle() +
                (ImportantPoints.getBallPolar().getAngle() > Math.PI / 2.0 ? -a : a);

        return actionTargetAngle;
    }

    @Override
    public boolean isRealState() {
        boolean check = ImportantPoints.getRobotPolar(RobotType.FRIEND_2).getRadius()
                < ImportantPoints.getBallPolar().getRadius();
        return check;
    }

    @Override
    public HorizVertSimpleDrive.BallTrackState getBallTrack() {
        return HorizVertSimpleDrive.BallTrackState.GO_NEXT_TO_BALL;
    }
}
