package strategy.states;

import PolarCoordNavigation.Coordinates.PolarCoordinate;
import strategy.drives.HorizVertSimpleDrive;
import strategy.points.ImportantPoints;
import vision.RobotType;

/**
 * Created by Luca & Levi
 */
public class HoverBRStateAvoidance extends State{

    public double distance;

    @Override
    public String getName() { return "HoverBRAvoidance"; }

    @Override
    public double getRadius() {
        return distance;
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

        boolean realState = false;

        PolarCoordinate friendPolarCoords = ImportantPoints.getRobotPolar(RobotType.FRIEND_1);
        PolarCoordinate foe1PolarCoords = ImportantPoints.getRobotPolar(RobotType.FOE_1);
        PolarCoordinate foe2PolarCoords = ImportantPoints.getRobotPolar(RobotType.FOE_2);

        PolarCoordinate[] players = {friendPolarCoords, foe1PolarCoords, foe2PolarCoords};
        RobotInPathState player = new RobotInPathState(null,"Nowhere");
        player.robotInPath(players, ImportantPoints.getRobotPolar(RobotType.FRIEND_2).getRadius(),
                ImportantPoints.getRobotPolar(RobotType.FRIEND_2).getAngle(), getRadius(), getAngle());

        boolean checkBehind = (ImportantPoints.getRobotPolar(RobotType.FRIEND_2).getRadius()
                > ImportantPoints.getBallPolar().getRadius());

        boolean checkRobot1 = player.getState().equals("In front") &&
                player.getPlayer().getRadius() <= (ImportantPoints.getBallPolar().getRadius() + 40);

        System.out.println(player.getState());

        if (!player.getState().equals("Nowhere")) {
            distance = 20 + player.getPlayer().getRadius();
            realState = checkBehind && checkRobot1;
        }

        return realState;
    }
}
