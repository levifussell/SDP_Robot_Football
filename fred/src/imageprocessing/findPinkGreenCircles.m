function [best_pinkgreen, count_pinkgreen, dist_pinkgreen] = findPinkGreenCircles(ImPatch, pinkThreshold, greenThreshold)

    %pinkCircle = zeros(3, 3, 3);
    %pinkCircle(:, :, 1) += [255 255 255; 255 255 255; 255 255 255];
    %pinkCircle(:, :, 3) += 150.0;
    pinkCircle = zeros(3, 3, 3);
    pinkCircle(:, :, 1) = [1 1 1; 1 1 1; 1 1 1];
    pinkCircle(:, :, 2) = [0.5 0.5 0.5; 0.5 0.5 0.5; 0.5 0.5 0.5];

    %greenCircle = double(zeros(3, 3, 3));
    %greenCircle(:, :, 2) += 255.0;
    greenCircle = zeros(3, 3, 3);
    greenCircle(:, :, 2) += 2.0;

    temps_pinkgreen = zeros(3, 3, 3, 2);
    temps_pinkgreen(:, :, :, 1) = pinkCircle;
    temps_pinkgreen(:, :, :, 2) = greenCircle;
    thresh_pinkgreen = [pinkThreshold, greenThreshold];

    [best_pinkgreen, count_pinkgreen] = findTemplatesInImage2(ImPatch, temps_pinkgreen, thresh_pinkgreen);

    %if count_pinkgreen(1, 1) < count_pinkgreen(2, 1)
        %roboSeg(best_pink(1, 1), best_pink(1, 2), :) = [255, 0, 150];
    %else
        %roboSeg(best_pink(2, 1), best_pink(2, 2), :) = [0, 255, 0];
    %end

end
