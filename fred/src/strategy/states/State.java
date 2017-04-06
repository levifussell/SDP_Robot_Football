package strategy.states;

import strategy.drives.HorizVertSimpleDrive;

/**
 * Created by Luca Mc & Levi
 */
public abstract class State {

    public State() {}

    public abstract String getName();

    public abstract double getRadius();

    public abstract double getAngle();

    public abstract boolean isRealState();

    public abstract HorizVertSimpleDrive.BallTrackState getBallTrack();
}
