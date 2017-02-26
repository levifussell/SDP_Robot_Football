function [best_blueyellow, count_blueyellow, dist_blueyellow] = findBlueYellowCircles(ImPatch, blueThreshold, yellowThreshold)

    % blue circle template
    %blueCircle = zeros(3, 3, 3);
    %blueCircle(:, :, 3) = [255 255 255; 255 255 255; 255 255 255];
    blueCircle = zeros(3, 3, 3) - 1;
    blueCircle(:, :, 3) = [1 1 1; 1 1 1; 1 1 1];

    % yellow circle template
    %yellowCircle = double(zeros(3, 3, 3));
    %yellowCircle(:, :, 1) = [0 0 0; 0 255 255; 0 255 255];
    %yellowCircle(:, :, 2) += 255.0;
    yellowCircle = double(zeros(3, 3, 3)) - 1;
    yellowCircle(:, :, 1) = [1 1 1; 1 1 1; 1 1 1];
    yellowCircle(:, :, 2) = [-1 -1 -1; -1 1 1; -1 1 1];

    % find templates
    temps_blueyellow = zeros(3, 3, 3, 2);
    temps_blueyellow(:, :, :, 1) = blueCircle;
    temps_blueyellow(:, :, :, 2) = yellowCircle;
    thresh_blueyellow = [blueThreshold, yellowThreshold];

    [best_blueyellow, count_blueyellow, dist_blueyellow] = findTemplatesInImage2(ImPatch, temps_blueyellow, thresh_blueyellow);
end
