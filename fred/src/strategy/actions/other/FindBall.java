package strategy.actions.other;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import strategy.Strategy;
import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.DynamicPoint;
import strategy.points.basicPoints.BallPoint;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

/**
 * Created by Luca Mc
 */
public class FindBall extends ActionBase {

    private double power;

    public FindBall(RobotBase robot) {
        super(robot);
        this.rawDescription = "Find Ball Action";
    }

    @Override
    public void enterState(int newState) {

        if (newState == 0){
            ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(0, 0, 0, 0);
        } else if (newState == 1){
            ((FourWheelHolonomicRobotPort) this.robot.port).fourWheelHolonomicMotion(power, power, -255, 255);
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        DynamicPoint ball = new BallPoint();
        ball.recalculate();
        if(us == null){
            this.enterState(0);
            return;
        }
        else if(ball == null){
            this.enterState(0);
        } else {
            if(this.state == 0){
                double ourAngle = us.location.direction;
                ourAngle = (Math.PI + Math.PI *2 + ourAngle) % (Math.PI * 2);
                VectorGeometry distToGoal = new VectorGeometry(ball.getX() - us.location.x, ball.getY() - us.location.y);
                double expectedAngle = (Math.atan2(distToGoal.y, distToGoal.x) + Math.PI) % (Math.PI * 2);
                double distToAngle = expectedAngle - ourAngle;
                if (Math.abs(distToAngle) < Math.PI / 30.0) {
                    this.enterState(0);
                } else {
                    double k = 15.0 / Math.PI;
                    double powerConst = 25 + Math.abs(distToAngle) * k;
                    power = distToAngle < 0 ? -powerConst : powerConst;
                    this.enterState(1);
                }
            }
        }
    }
}
