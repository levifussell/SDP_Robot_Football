function [robotPos, robotAngle] = runTracker(image, previousRobotPos, templates, searchRadius)
    
    [robotPos, TCount, TBestDist, robotAngle] = matchTemplateSet(image, templates, previousRobotPos(1), previousRobotPos(2), searchRadius);
    
end
