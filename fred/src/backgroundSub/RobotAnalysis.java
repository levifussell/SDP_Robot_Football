package backgroundSub;

import vision.Robot;
import vision.tools.DirectedPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jinhong on 14/03/2017.
 */
public class RobotAnalysis {

    public HashMap<RobotTeam,Robot> robots = new HashMap<RobotTeam,Robot>();
    public static RobotAnalysis robot_Analysis = new RobotAnalysis();
    public static List<Robot> unfound_robot;
    public static List<List<String>> unfound_plate;
    private static boolean check_blue_red, check_blue_green;
    private static boolean check_yellow_red, check_yellow_green;


    public RobotAnalysis(){}

    public void teamAnalysis(List<Robot> robot, List<List<String>> plate_color)
    {
        if(robot.size() != plate_color.size()) return;
        for(int i =0; i < robot.size(); i++)
        {
            RobotTeam rt = plateAnalysis(plate_color.get(i));
            if(rt != RobotTeam.UNKNOW)
            {
                robots.put(rt,robot.get(i));
            }
            else
            {
                unfound_robot.remove(robot.get(i));
                unfound_plate.remove(plate_color.get(i));
                i --;
            }
        }
    }

    public void robotAnalysis(List<DirectedPoint>robot_location, List<List<String>> plate_color)
    {
        check_blue_red = false;
        check_blue_green = false;
        check_yellow_red = false;
        check_yellow_green = false;
        unfound_robot = new ArrayList<Robot>();
        unfound_plate = new ArrayList<List<String>>();
        if(robot_location.size() != plate_color.size()) return;
        for(int i =0; i < robot_location.size(); i++)
        {
            Robot robot = new Robot();
            robot.location = robot_location.get(i);
            RobotTeam rt = plateAnalysis(plate_color.get(i));
            if(rt != RobotTeam.UNKNOW)
            {
                robots.put(rt,robot);
            }
            else
            {
                unfound_robot.add(robot);
                unfound_plate.add(plate_color.get(i));
            }
        }
        while(!(check_blue_green&&check_blue_red&&check_yellow_green&&check_yellow_red) && plate_color.size() == 4)
        {
            teamAnalysis(unfound_robot,unfound_plate);
        }

    }

    public RobotTeam plateAnalysis(List<String> plate_color)
    {
        boolean check_yellow = false, check_blue = false;
        boolean check_green3 = false, check_red3 = false;
        boolean check_green = false, check_red = false;
        for(String s : plate_color)
        {
            if(s.equals("blue"))
            {
                check_blue = true;
            }
            else
            {
                if(s.equals("yellow"))
                {
                    check_yellow = true;
                }
            }
            if(s.equals("green3"))
            {
                check_green3 = true;
            }
            else
            {
                if(s.equals("red3"))
                {
                    check_red3 = true;
                }
            }
            if(s.equals("green"))
            {
                check_green = true;
            }
            else
            {
                if(s.equals("red"))
                {
                    check_red = true;
                }
            }

        }
        if(check_blue && check_green && check_red3)
        {
            this.check_blue_green = true;
            return RobotTeam.BLUE_GREEN;
        }

        if(check_blue && check_green3 && check_red)
        {
            this.check_blue_red = true;
            return RobotTeam.BLUE_RED;
        }

        if(check_yellow && check_green3 && check_red)
        {
            this.check_yellow_red = true;
            return RobotTeam.YELLOW_RED;
        }

        if(check_yellow && check_green && check_red3)
        {
            this.check_yellow_green = true;
            return RobotTeam.YELLOW_GREEN;
        }

        if(check_green && check_red3 && !check_blue && !check_yellow)
        {
            if(check_blue_green)
            {
                this.check_yellow_green = true;
                return RobotTeam.YELLOW_GREEN;
            }
            else
            {
                if(check_yellow_green)
                {
                    this.check_blue_green = true;
                    return RobotTeam.BLUE_GREEN;
                }
            }
        }

        if(check_green3 && check_red && !check_blue && !check_yellow)
        {
            if(check_blue_red)
            {
                this.check_yellow_red = true;
                return RobotTeam.YELLOW_RED;
            }
            else
            {
                if(check_yellow_red)
                {
                    this.check_blue_red = true;
                    return RobotTeam.BLUE_RED;
                }
            }
        }

        if(check_green && check_red && check_yellow)
        {
            if(check_yellow_green)
            {
                check_yellow_red = true;
                return RobotTeam.YELLOW_RED;
            }
            else
            {
                if(check_yellow_red)
                {
                    check_yellow_green = true;
                    return RobotTeam.YELLOW_GREEN;
                }
            }
        }

        if(check_green && check_red && check_blue)
        {
            if(check_blue_green)
            {
                check_blue_red = true;
                return RobotTeam.BLUE_RED;
            }
            else
            {
                if(check_blue_red)
                {
                    check_blue_green = true;
                    return RobotTeam.BLUE_GREEN;
                }
            }
        }

        if(check_green && check_red && !check_blue && !check_yellow)
        {
            if(check_blue_green && !check_yellow_green && check_blue_red && check_yellow_red)
            {
                check_yellow_green = true;
                return RobotTeam.YELLOW_GREEN;
            }
            if(check_blue_green && check_yellow_green && check_blue_red && !check_yellow_red)
            {
                check_yellow_red = true;
                return RobotTeam.YELLOW_RED;
            }
            if(!check_blue_green && check_yellow_green && check_blue_red && check_yellow_red)
            {
                check_blue_green = true;
                return RobotTeam.BLUE_GREEN;
            }
            if(check_blue_green && check_yellow_green && !check_blue_red && check_yellow_red)
            {
                check_blue_red = true;
                return RobotTeam.BLUE_RED;
            }
        }
        return RobotTeam.UNKNOW;
    }

}
