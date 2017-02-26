function [robotPos, robotAngle, robotColour] = findRobot(ImPatch, blueThreshold, yellowThreshold, pinkThreshold, greenThreshold, debug=false, id=0)

    % robot layout
    robotLayout = double(zeros(10, 10, 3));

    % find blue/yellow circles
    t1 = time();
    [best_blueyellow, count_blueyellow] = findBlueYellowCircles(ImPatch, blueThreshold, yellowThreshold);
    disp("time to find blue/yellow "), disp((time() - t1) * 1000.0)
    t1 = time();
    countThresh = 0;
    isBlue = false;
    isYellow = false;
    isPink = false;
    isGreen = false;

	%if(debug)
	    %figure(id)
	    %ImPatch(best_blueyellow(1, 1), best_blueyellow(1, 2), :) = [255, 255, 255];
	    %ImPatch(best_blueyellow(2, 1), best_blueyellow(2, 2), :) = [255, 255, 0];
	    %imagesc(ImPatch)
	%end

    % offset the postion by 1 to centre it
    origin = best_blueyellow .+ 1;
    origin_colour = origin(1, :);
    % if robot is BLUE:
    if count_blueyellow(1) > countThresh
        isBlue = true;
        origin_colour = origin(1, :);
    end

    % if robot is YELLOW:
    if count_blueyellow(2) > countThresh
        isYellow = true;
        origin_colour = origin(2, :);
    end

    % crop the robot via the channel
    rowStart = max(1, origin_colour(1, 1) - floor(size(robotLayout, 1) / 2.0));
    rowEnd = min(size(ImPatch, 1), origin_colour(1, 1) + ceil(size(robotLayout, 1) / 2.0) - 1);
    colStart = max(1, origin_colour(1, 2) - floor(size(robotLayout, 2) / 2.0));
    colEnd = min(size(ImPatch, 2), origin_colour(1, 2) + ceil(size(robotLayout, 2) / 2.0) - 1);
    roboSeg = ImPatch(rowStart:rowEnd, colStart:colEnd, :);

    disp("time to crop robot image patch "), disp((time() - t1) * 1000.0)
    t1 = time();
    
    % translate the blue/yellow position to the smaller domain
    %robotBinary = zeros(size(ImPatch, 1), size(ImPatch, 2), 1);
    %robotBinary(origin_colour(1, 1), origin_colour(1, 2)) = 1;
    %robotBinary = robotBinary(rowStart:rowEnd, colStart:colEnd);
    %[i, j] = find(robotBinary);
    %origin_colour_scale = [i, j]
    % scaled origin is always the centre of the robot layout
    origin_colour_scale = [size(robotLayout, 1) / 2 + 1, size(robotLayout, 2) / 2 + 1];

    % find pink/green circles
    [best_pinkgreen, count_pinkgreen] = findPinkGreenCircles(roboSeg, pinkThreshold, greenThreshold);

    disp("time to find pink/green "), disp((time() - t1) * 1000.0)
    t1 = time();

    angle_colour = best_pinkgreen(1, :);
    % if robot is PINK
    if count_pinkgreen(1, 1) < count_pinkgreen(2, 1)
        isPink = true;
        angle_colour = best_pinkgreen(1, :);
    % if robot is GREEN
    else
        isGreen = true;
        angle_colour = best_pinkgreen(2, :);
    end

	if(debug)
	    figure(id * id)
	    roboSeg(best_pinkgreen(1, 1), best_pinkgreen(1, 2), :) = [255, 0, 150];
	    roboSeg(best_pinkgreen(2, 1), best_pinkgreen(2, 2), :) = [0, 255, 0];
	    imagesc(roboSeg)
	end
    disp("time to draw final robot crop image "), disp((time() - t1) * 1000.0)
    t1 = time();

    if (isGreen || isPink) && (isBlue || isYellow)
        % calculate robot angle
        %origin_colour_scale
        %angle_colour
        distToColour = origin_colour_scale - angle_colour;

        offsetAngleToRobotFront = 0.0;
        robotAngle = atan2(distToColour(1, 1), distToColour(1, 2));

        % calculate robot position (its just the blue/yellow position)
        robotPos = origin_colour;

        robotColour = 0;

        if isBlue
            robotColour = 10;
        elseif isYellow
            robotColour = 20;
        else
            robotColour = 0;
        end

        if isPink
            robotColour += 1;
        elseif isGreen
            robotColour += 2;
        else
            robotColour += 0;
        end

    else
        robotAngle = -1;
        robotColour = -1;
        robotPos = [-1, -1];
    end

    disp("time to calculate final robot values "), disp((time() - t1) * 1000.0)
    t1 = time();

end
