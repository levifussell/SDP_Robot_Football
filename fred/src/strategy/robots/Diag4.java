package strategy.robots;

import communication.ports.robotPorts.Diag4RobotPort;
import strategy.controllers.diag4.SpinnerKickController;
import strategy.drives.FourWheelHolonomicDrive;
import vision.RobotType;

/**
 * Created by levif
 */
public class Diag4 extends RobotBase  {

  public final SpinnerKickController SPINNERKICK_CONTROLLER = new SpinnerKickController(this);

  public Diag4(RobotType robotType)
  {
    super(robotType, new Diag4RobotPort(), new FourWheelHolonomicDrive());
    this.controllers.add(this.SPINNERKICK_CONTROLLER);
  }

  @Override
  public void performManual() { /*nothing here for now*/ }
}
