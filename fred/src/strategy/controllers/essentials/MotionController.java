package strategy.controllers.essentials;

import strategy.Strategy;
import strategy.controllers.ControllerBase;
import strategy.points.DynamicPoint;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.RobotType;

/**
 * Created by Simon Rovder
 */
public class MotionController extends ControllerBase {

    public MotionMode mode;
    private DynamicPoint heading = null;
    private DynamicPoint destination = null;

    private int tolerance;

    public MotionController(RobotBase robot) {
        super(robot);
    }

    public enum MotionMode{
        ON, OFF
    }

    public void setMode(MotionMode mode){
        this.mode = mode;
    }

    public void setTolerance(int tolerance){
        this.tolerance = tolerance;
    }

    public void setDestination(DynamicPoint destination){
        this.destination = destination;
    }

    public void setHeading(DynamicPoint dir){
        this.heading = dir;
    }

    public void perform(){
        if(this.mode == MotionMode.OFF) return;


<<<<<<< HEAD
//        NavigationInterface navigation;
//
//        VectorGeometry heading = null;
//        VectorGeometry destination = null;
//
//
//
//        if(this.destination != null){
//            this.destination.recalculate();
//
//            destination = new VectorGeometry(this.destination.getX(), this.destination.getY());
//
//            //for now we do not care about maneuvering around obstacles (navigating)
//            navigation = new PotentialFieldNavigation();
//            GUI.gui.searchType.setText("Potential Fields");
//            navigation.setDestination(new VectorGeometry(destination.x, destination.y));
//
//
//
////            boolean intersects = false;
//
////            for(Obstacle o : this.obstacles){
////                intersects = intersects || o.intersects(us.location, destination);
////            }
////
////            for(Robot r : Strategy.world.getRobots()){
////                if(r != null && r.type != RobotType.FRIEND_2){
////                    intersects = intersects || VectorGeometry.vectorToClosestPointOnFiniteLine(us.location, destination, r.location).minus(r.location).length() < 30;
////                }
////            }
////
////            if(intersects || us.location.distance(destination) > 30){
////                navigation = new AStarNavigation();
////                GUI.gui.searchType.setText("A*");
////            } else {
////                navigation = new PotentialFieldNavigation();
////                GUI.gui.searchType.setText("Potential Fields");
////            }
////
////            navigation.setDestination(new VectorGeometry(destination.x, destination.y));
//
//
//        } else {
//            return;
//        }
//
//        if(this.heading != null){
//            this.heading.recalculate();
//            heading = new VectorGeometry(this.heading.getX(), this.heading.getY());
//        } else heading = VectorGeometry.fromAngular(us.location.direction, 10, null);
//
//
//
////        if(this.obstacles != null){
////            navigation.setObstacles(this.obstacles);
////        }
//
//
//
//        VectorGeometry force = navigation.getForce();
//        if(force == null){
//            this.robot.port.stop();
//            return;
//        }
//
//        VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction, 10, null);
//        VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, heading);
//        //robotHeading = robotHeading.add(robotHeading.rotate(Math.PI / 4));
//        double factor = 1;
//        double rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);
//        // Can throw null without check because null check takes SourceGroup into consideration.
//        if(destination.distance(us.location) < 30){
//            factor = 0.7;
//        }
//        if(this.destination != null && us.location.distance(destination) < tolerance){
//            this.robot.port.stop();
//            return;
//        }

        double rotation = 0.0; //we don't care about this parameter right now
        double factor = 0.0; //we don't care about this parameter right now
        VectorGeometry ourRobotLocation = new VectorGeometry(0, 0);
        DirectedPoint target = new DirectedPoint(this.destination.getX(), this.destination.getY(), 0);

//        strategy.navigationInterface.draw();

        this.robot.drive.move(this.robot.port, target, ourRobotLocation, rotation, factor);

=======
        this.robot.drive.move(this.robot.port);
>>>>>>> 1f3673266cbedaac1e44fae73e8bc03510da1625
    }
}
