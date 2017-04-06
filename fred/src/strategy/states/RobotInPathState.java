package strategy.states;

import PolarCoordNavigation.Coordinates.PolarCoordinate;
import strategy.Strategy;
import strategy.points.ImportantPoints;
import vision.Robot;
import vision.RobotType;

/**
 * Created by s1442231 on 05/04/17.
 */
public class RobotInPathState {

    private PolarCoordinate player;
    private String whereIsTheRobot;

    public RobotInPathState(PolarCoordinate player, String whereIsTheRobot) {
        this.player = player;
        this.whereIsTheRobot = whereIsTheRobot;
    }

    public PolarCoordinate getPlayer() {
        return this.player;
    }

    public String getState() {
        return this.whereIsTheRobot;
    }

    public void robotInPath(double ourRadius, double ourAngle,
                            double targetRadius, double targetAngle) {

        // check if robot is in the destination path of diag4 and where
        final double angleThreshold = Math.PI / 40;
//        PolarCoordinate friendPolarCoords = ImportantPoints.getRobotPolar(RobotType.FRIEND_1);
//        PolarCoordinate foe1PolarCoords = ImportantPoints.getRobotPolar(RobotType.FOE_1);
//        PolarCoordinate foe2PolarCoords = ImportantPoints.getRobotPolar(RobotType.FOE_2);

        RobotType[] robots = {RobotType.FRIEND_1, RobotType.FOE_1, RobotType.FOE_2};
        for (RobotType rob : robots) {
            PolarCoordinate robot = ImportantPoints.getRobotPolar(rob);
            if (Strategy.world.getRobot(rob) == null) {
                this.whereIsTheRobot = "Nowhere";
            } else {
                double targetAngleDiff = Math.abs(robot.getAngle() - targetAngle);
                double robotsAngleDiff = Math.abs(robot.getAngle() - ourAngle);
                if (ourRadius < robot.getRadius() + 10) {
                    this.whereIsTheRobot = "Nowhere";
                } else if (targetAngleDiff <= angleThreshold /*&& robotsAngleDiff <= angleThreshold*/) {
                    if (robot.getRadius() < ourRadius && robot.getRadius() >= ImportantPoints.getBallPolar().getRadius()) {
                        this.player = robot;
                        System.out.println("robot (" + rob.toString() +  ") location: " + robot.toString());
                        this.whereIsTheRobot = "In front";
//                        this.whereIsTheRobot = "Nowhere";
                        break;
                    } else if (robot.getRadius() < ImportantPoints.getBallPolar().getRadius()) {
                        this.player = robot;
                        this.whereIsTheRobot = "Behind";
                        this.whereIsTheRobot = "Nowhere";
//                        break;
                    }
                } else if (robot.getAngle() < Math.max(ourAngle, targetAngle) && robot.getAngle() > Math.min(ourAngle, targetAngle)) {
                    if (robot.getRadius() >= targetRadius && robot.getRadius() < ourRadius) {
                        this.player = robot;
//                        this.whereIsTheRobot = "In front";
                        this.whereIsTheRobot = "Nowhere";
//                        break;
                    }
                }
            }
        }
    }
}