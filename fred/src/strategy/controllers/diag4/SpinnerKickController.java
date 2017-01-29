package strategy.controllers.diag4;

import communication.ports.interfaces.SpinnerKickRobotPort;
import strategy.Strategy;
import strategy.controllers.ControllerBase;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.RobotType;

/**
 * Created by levif
 */
public class SpinnerKickController extends ControllerBase {

  //wait 1 second after the spinner has started before engaging it (for now)
  private final long delayMilliToEngageSpinnerkick = 1000;
  private long startTimeMilliSpinnerkick;

  //current state of the spinnerkick
  private int engaged;
  private int spinning;

  public SpinnerKickController(RobotBase robot)
  {
    super(robot);
    this.startTimeMilliSpinnerkick = -1;
    //start state is no moving up/down and no spinning
    this.engaged = 0;
    this.spinning = 0;
  }

  @Override
  public void setActive(boolean active)
  {
    super.setActive(active);
    this.startTimeMilliSpinnerkick = System.currentTimeMillis();
  }

  private void sendSpinkickPort(int spin, int engage, boolean overrideState)
  {
    //first check the new state call isn't already the current state of the spinnerkick
    if(spin == this.spinning && !overrideState)
      spin = 0; //keep the current state

    if(engage == this.engaged && !overrideState)
      engage = 0; //keep the current state

    SpinnerKickRobotPort spinkickPort = (SpinnerKickRobotPort)this.robot.port;
    spinkickPort.spinnerKick(spin, engage);
  }

  // turn the spinner on, but do not move it down
  public void runSpinnerToPrepareEngage(boolean overrideState)
  {
    // record the time we have started the spinner
    this.startTimeMilliSpinnerkick = System.currentTimeMillis();
    this.sendSpinkickPort(1, 0, overrideState);
  }

  // turn the spinner on (if not already) and move it down
  public void engageSpinner(boolean overrideState)
  {
    this.sendSpinkickPort(1, 1, overrideState);
  }

  // turn the spinner off and move it up
  public void disengageSpinner(boolean overrideState)
  {
    // we reset the time we start the robot spinner kicking
    this.startTimeMilliSpinnerkick = -1;
    this.sendSpinkickPort(0, -1, overrideState);
  }

  @Override
  public void perform()
  {
    //check that we are a spinnerkicker port
    assert(this.robot.port instanceof SpinnerKickRobotPort);

    //get our robot from the world
    Robot ourRobot = Strategy.world.getRobot(this.robot.robotType);

    //check the robot exists and running
    if(ourRobot != null)
    {
      if(this.isActive())
      {
        //PLAN: we want to drive the wheel if we are the probable
        //  ball holder (for now. later we might want to implement it
        //  if we are NEAR the ball).
        RobotType robotTypeWithBall = Strategy.world.getProbableBallHolder();

        //our robot seems to be considered as FRIEND_2 in this codebase.
        //  we should look into changing this later to be more clear.
        RobotType OUR_ROBOT = RobotType.FRIEND_2;

        if(robotTypeWithBall == OUR_ROBOT)
        {
          long currentTimeMilli = System.currentTimeMillis();

          if(this.startTimeMilliSpinnerkick == -1)
          {
            this.runSpinnerToPrepareEngage(false);
          }
          else if(currentTimeMilli - this.startTimeMilliSpinnerkick > this.delayMilliToEngageSpinnerkick)
          {
            this.engageSpinner(false);
          }
        }
        else
        {
          disengageSpinner(false);
        }

      }
    }
  }
}
