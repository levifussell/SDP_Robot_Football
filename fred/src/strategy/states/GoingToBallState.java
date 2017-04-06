package strategy.states;

import strategy.drives.HorizVertSimpleDrive;
import strategy.points.ImportantPoints;
import vision.RobotType;

/**
 * Created by Luca & Levi
 */
public class GoingToBallState extends State {

    @Override
    public String getName() { return "GoingToBall"; }

    @Override
    public double getRadius() {
        return ImportantPoints.getBallPolar().getRadius();
    }

    @Override
    public double getAngle() {
        return ImportantPoints.getBallPolar().getAngle();
    }

    @Override
    public boolean isRealState() {
        double angleDiff = ImportantPoints.getBallPolar().getAngle() -
                ImportantPoints.getRobotPolar(RobotType.FRIEND_2).getAngle();
        double angleDiffAbs = Math.abs(angleDiff);

        boolean check = (angleDiffAbs < (Math.PI / 40) &&
                ImportantPoints.getRobotPolar(RobotType.FRIEND_2).getRadius() > ImportantPoints.getBallPolar().getRadius());
        return check;
    }

    @Override
    public HorizVertSimpleDrive.BallTrackState getBallTrack() {
        return HorizVertSimpleDrive.BallTrackState.GO_TO_BALL;
    }
}
