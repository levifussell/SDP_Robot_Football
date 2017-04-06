package strategy.states;

import PolarCoordNavigation.Coordinates.PolarCoordinate;
import strategy.points.ImportantPoints;

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

    public void robotInPath(PolarCoordinate[] robots, double ourRadius,
                            double ourAngle, double targetRadius, double targetAngle) {

        // check if robot is in the destination path of diag4 and where
        final double angleThreshold = Math.PI / 40;

        for (PolarCoordinate robot : robots) {
            if (robot == null) {
                this.whereIsTheRobot = "Nowhere";
            } else {
                double targetAngleDiff = Math.abs(robot.getAngle() - targetAngle);
                double robotsAngleDiff = Math.abs(robot.getAngle() - ourAngle);
                if (ourRadius < robot.getRadius() + 10) {
                    this.whereIsTheRobot = "Nowhere";
                } else if (targetAngleDiff <= angleThreshold && robotsAngleDiff <= angleThreshold) {
                    if (robot.getRadius() < ourRadius && robot.getRadius() >= ImportantPoints.getBallPolar().getRadius()) {
                        this.player = robot;
                        this.whereIsTheRobot = "In front";
                        break;
                    } else if (robot.getRadius() < ImportantPoints.getBallPolar().getRadius()) {
                        this.player = robot;
                        this.whereIsTheRobot = "Behind";
                        break;
                    }
                } else if (robot.getAngle() < Math.max(ourAngle, targetAngle) && robot.getAngle() > Math.min(ourAngle, targetAngle)) {
                    if (robot.getRadius() >= targetRadius && robot.getRadius() < ourRadius) {
                        this.player = robot;
                        this.whereIsTheRobot = "In front";
                        break;
                    }
                }
            }
        }
    }
}