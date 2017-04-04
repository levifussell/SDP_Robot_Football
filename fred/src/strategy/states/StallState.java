package strategy.states;

import strategy.drives.HorizVertSimpleDrive;
import strategy.points.ImportantPoints;
import vision.RobotType;

/**
 * Created by Luca & Levi
 */
public class StallState extends State {

    @Override
    public double getRadius() {
        return ImportantPoints.getRobotPolar(RobotType.FRIEND_2).getRadius();
    }

    @Override
    public double getAngle() {
        return ImportantPoints.getRobotPolar(RobotType.FRIEND_2).getAngle();
    }

    @Override
    public HorizVertSimpleDrive.BallTrackState getBallTrack() {
        return HorizVertSimpleDrive.BallTrackState.UNKNOWN;
    }

    @Override
    public boolean isRealState() {
        double angleDiff = ImportantPoints.getBallPolar().getAngle() -
                ImportantPoints.getRobotPolar(RobotType.FRIEND_2).getAngle();
        double angleDiffAbs = Math.abs(angleDiff);

        boolean check1 = (angleDiffAbs < (Math.PI / 40) &&
                ImportantPoints.getRobotPolar(RobotType.FRIEND_2).getRadius() > ImportantPoints.getBallPolar().getRadius());
        boolean check2 = ImportantPoints.getRobotPolar(RobotType.FRIEND_2).getRadius()
                < ImportantPoints.getBallPolar().getRadius();
        boolean check3 = ImportantPoints.getRobotPolar(RobotType.FRIEND_2).getRadius()
                < ImportantPoints.getBallPolar().getRadius();

        return !check1 && !check2 && !check3;
    }
}
