package strategy.states;

import PolarCoordNavigation.Coordinates.PolarCoordinate;
import strategy.points.ImportantPoints;
import vision.RobotType;

import java.util.ArrayList;

/**
 * Created by Luca & Levi
 */
public class StateManager {

    private static ArrayList<State> states = new ArrayList<>();
    private static State currentState;

    public static void addState(State state) {
        states.add(state);
    }

    public static State getCurrentState() {
        return currentState;
    }

    public static void update() {
        for (State s: states) {
            if (s.isRealState()){
                currentState = s;
                System.out.println(currentState.getName());
                break;
            }
        }
    }
}
