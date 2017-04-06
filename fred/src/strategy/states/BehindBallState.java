package strategy.states;

import strategy.drives.HorizVertSimpleDrive;
import strategy.points.ImportantPoints;
import vision.RobotType;

/**
 * Created by Luca & Levi
 */
public class BehindBallState extends State {

    @Override
    public String getName() { return "BehindBall"; }

    @Override
    public double getRadius() {
        return ImportantPoints.getBallPolar().getRadius() + 30;
    }

    @Override
    public double getAngle() {
        return ImportantPoints.getBallPolar().getAngle();
    }

    @Override
    public HorizVertSimpleDrive.BallTrackState getBallTrack() {
        return HorizVertSimpleDrive.BallTrackState.GO_BEHIND_BALL;
    }


    @Override
    public boolean isRealState() {
        boolean check = (ImportantPoints.getRobotPolar(RobotType.FRIEND_2).getRadius()
                > ImportantPoints.getBallPolar().getRadius());
        return check;
    }
}
