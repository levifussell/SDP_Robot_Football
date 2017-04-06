package strategy.states;

import PolarCoordNavigation.Coordinates.PolarCoordinate;
import strategy.drives.HorizVertSimpleDrive;
import strategy.points.ImportantPoints;
import vision.RobotType;

/**
 * Created by Luca & Levi
 */
public class NTBStateAvoidance extends State {

    @Override
    public String getName() { return "NTBAvoidance"; }

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
    public HorizVertSimpleDrive.BallTrackState getBallTrack() {
        return HorizVertSimpleDrive.BallTrackState.GO_NEXT_TO_BALL;
    }

    @Override
    public boolean isRealState() {

        boolean realState = false;

//        PolarCoordinate friendPolarCoords = ImportantPoints.getRobotPolar(RobotType.FRIEND_1);
//        PolarCoordinate foe1PolarCoords = ImportantPoints.getRobotPolar(RobotType.FOE_1);
//        PolarCoordinate foe2PolarCoords = ImportantPoints.getRobotPolar(RobotType.FOE_2);
//
//        PolarCoordinate[] players = {friendPolarCoords, foe1PolarCoords, foe2PolarCoords};
        RobotInPathState player = new RobotInPathState(null,"Nowhere");
        player.robotInPath(ImportantPoints.getRobotPolar(RobotType.FRIEND_2).getRadius(),
                ImportantPoints.getRobotPolar(RobotType.FRIEND_2).getAngle(), getRadius(), getAngle());

        boolean check = ImportantPoints.getRobotPolar(RobotType.FRIEND_2).getRadius()
                < ImportantPoints.getBallPolar().getRadius();


        boolean checkRobot = player.getState().equals("In front") &&
                player.getPlayer().getRadius() > (ImportantPoints.getBallPolar().getRadius() + 40);

        System.out.println(player.getState());

        if(player.getState().equals("Nowhere")) {
            realState = check;
        } else {
            realState = check || checkRobot;
        }

        return realState;
    }
}
