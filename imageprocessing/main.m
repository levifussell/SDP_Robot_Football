I1 = imread("imgs/snap-unknown-20170126-134652-1.jpeg");
%I1 = imread("imgs/snap-unknown-20170126-134705-1.jpeg");
I1 = imread("imgs/snap-unknown-20170206-180525-1.jpeg");

[I1p, patches] = process_image(I1, false);
%figure(20)
%imagesc(I1p)
%patches
patchRGB = zeros(3, size(patches, 3));
patchSizes = zeros(1, size(patches, 3));
% draw each patch
for j=1:3%size(patches, 3)
    %imagesc(I1p(patches(2, 2, i):patches(1, 1, i), patches(1, 2, i):patches(2, 1, i), :))
    ImPatch = I1p(patches(2, 2, j):patches(1, 1, j), patches(1, 2, j):patches(2, 1, j), :);
    %figure(j + 1000)
    %imagesc(ImPatch);
    %ImPatch = ImPatch .* repmat(var(ImPatch, 1, 3) > 2000, [1, 1, 3]);
    patchRGB(1, j) = sum(sum(ImPatch(:, :, 1)));
    patchRGB(2, j) = sum(sum(ImPatch(:, :, 2)));
    patchRGB(3, j) = sum(sum(ImPatch(:, :, 3)));
    patchSizes(j) = size(ImPatch, 1) .* size(ImPatch, 2);

    % run each image patch through a circle search
    % red circle
    redCircle = double(zeros(3, 3, 3));
    redCircle(:, :, 1) += 255.0;
    greenCircle = double(zeros(3, 3, 3));
    greenCircle(:, :, 2) += 255.0;
    % blue circles have a slight green edge
    %  (because they are in the middle of the robot)
    blueCircle = zeros(3, 3, 3);
    %blueCircle(:, :, 2) = [255 255 255; 255 0 255; 255 0 255];
    %blueCircle(:, :, 1) -= 255.0;
    %blueCircle(:, :, 2) -= 255.0;
    blueCircle(:, :, 3) = [255 255 255; 255 255 255; 255 255 255];
    yellowCircle = double(zeros(3, 3, 3));
    % yellow circles have a slight green edge
    %  (because they are in the middle of the robot)
    yellowCircle(:, :, 1) = [0 0 0; 0 255 255; 0 255 255];
    yellowCircle(:, :, 2) += 255.0;
    pinkCircle = zeros(3, 3, 3);
    pinkCircle(:, :, 1) += [255 255 255; 255 255 255; 255 255 255];
    %pinkCircle(:, :, 2) += [255 255 255; 255 0 0; 255 0 0];
    pinkCircle(:, :, 3) += 150.0;

    % robot layout
    robotLayout = double(zeros(9, 9, 3));
    % pink = (255, 62, 150)
    % red channel
    robotLayout(:, :, 1) = [255 255 255 0 0 0 255 255 255;
                            255 255 255 0 0 0 255 255 255;
                            255 255 255 0 0 0 255 255 255;
                            0 0 0 0 0 0 0 0 0;
                            0 0 0 0 0 0 0 0 0;
                            0 0 0 0 0 0 0 0 0;
                            0 0 0 0 0 0 255 255 255;
                            0 0 0 0 0 0 255 255 255;
                            0 0 0 0 0 0 255 255 255;];
    % blue channel
    robotLayout(:, :, 2) = [0 0 0 255 255 255 0 0 0;
                            0 0 0 255 255 255 0 0 0;
                            0 0 0 255 255 255 0 0 0;
                            255 255 255 0 0 0 255 255 255;
                            255 255 255 0 0 0 255 255 255;
                            255 255 255 0 0 0 255 255 255;
                            255 255 255 255 255 255 0 0 0;
                            255 255 255 255 255 255 0 0 0;
                            255 255 255 255 255 255 0 0 0;];

    % green channel
    robotLayout(:, :, 3) = [150 150 150 0 0 0 150 150 150;
                            150 150 150 0 0 0 150 150 150;
                            150 150 150 0 0 0 150 150 150;
                            0 0 0 255 255 255 0 0 0;
                            0 0 0 255 255 255 0 0 0;
                            0 0 0 255 255 255 0 0 0;
                            0 0 0 0 0 0 150 150 150;
                            0 0 0 0 0 0 150 150 150;
                            0 0 0 0 0 0 150 150 150;];


    %robotLayoutRot = rotateImage(robotLayout, 45.0);
    %figure(100)
    %imagesc(robotLayoutRot)
    %figure(101)
    %imagesc(robotLayout)

    % create a selection of rotated robotLayouts
    numRobotLayouts = 8;
    degreeRate = 360.0 / numRobotLayouts;
    robotLayoutsRot = zeros(9, 9, 3, numRobotLayouts);
    for i=1:numRobotLayouts
        robotLayoutsRot(:, :, :, i) = rotateImage(robotLayout, degreeRate * (i + 1));
        %figure(100 + i)
        %imagesc(robotLayoutsRot(:, :, :, i))
        robotLayoutsRot(:, :, :, i) += (robotLayoutsRot(:, :, :, i) == 0) * 1500.0;
    end

    distThresholdRed = 1400;
    distThresholdGreen = 2500;
    distThresholdBlue = 2000000;
    distThresholdYellow = 400000;
    distThresholdPink = 400000;
    redCircleCount = 0;
    greenCircleCount = 0;
    yellowCircleCount = 0;
    blueCircleCount = 0;
    pinkCircleCount = 0;
    ImPatchD = double(ImPatch);

    bestBlueCirclePos = [1; 1];
    bestBlueCircleDist = 1000000000;
    bestYellowCirclePos = [2; 1];
    bestYellowCircleDist = 1000000000;
    bestPinkCirclePos = [3; 1];
    bestPinkCircleDist = 1000000000;

    temps_blueyellow = zeros(3, 3, 3, 2);
    temps_blueyellow(:, :, :, 1) = blueCircle;
    temps_blueyellow(:, :, :, 2) = yellowCircle;
    thresh_blueyellow = [distThresholdBlue, distThresholdYellow];

    [best_blueyellow, count_blueyellow] = findTemplatesInImage(ImPatch, temps_blueyellow, thresh_blueyellow);

    ImPatch(best_blueyellow(1, 1), best_blueyellow(1, 2), :) = [0, 0, 255];
    ImPatch(best_blueyellow(2, 1), best_blueyellow(2, 2), :) = [255, 255, 0];

    rowStart = max(1, best_blueyellow(1, 1) - floor(size(robotLayout, 1) / 2.0))
    rowEnd = min(size(ImPatch, 1), best_blueyellow(1, 1) + ceil(size(robotLayout, 1) / 2.0) - 1)
    colStart = max(1, best_blueyellow(1, 2) - floor(size(robotLayout, 2) / 2.0))
    colEnd = min(size(ImPatch, 2), best_blueyellow(1, 2) + ceil(size(robotLayout, 2) / 2.0) - 1)
    roboSeg = ImPatch(rowStart:rowEnd, colStart:colEnd, :);

    temps_pink = zeros(3, 3, 3, 1);
    temps_pink(:, :, :, 1) = pinkCircle;
    thresh_pink = [distThresholdPink];

    [best_pink] = findTemplatesInImage(roboSeg, temps_pink, thresh_pink);

    roboSeg(best_pink(1, 1), best_pink(1, 2), :) = [255, 0, 150];

    %figure(j + 2000)
    %imagesc(roboSeg)
    %ImPatch(bestBlueCirclePos(1, 1), bestBlueCirclePos(2, 1), :) = [0, 0, 255];
    %ImPatch(bestYellowCirclePos(1, 1), bestYellowCirclePos(2, 1), :) = [255, 255, 0];
    %ImPatch(bestPinkCirclePos(1, 1), bestPinkCirclePos(2, 1), :) = [255, 0, 150];

    %for r=1:(size(ImPatch, 1) - size(redCircle, 1))
        %for c = 1:(size(ImPatch, 2) - size(redCircle, 2))
            %circleSeg = ImPatchD(r:(r + size(redCircle, 1) - 1), c:(c + size(redCircle, 2) - 1), :);


            %% red circle
            %distSeg = sum(sum(sum(abs(circleSeg - redCircle) .^ 2)));
            %if distSeg <= distThresholdRed
                %redCircleCount += 1;
            %end

            %% green circle
            %distSeg = sum(sum(sum(abs(circleSeg - greenCircle) .^ 2)));
            %if distSeg <= distThresholdGreen
                %greenCircleCount += 1;
            %end

            %% blue circle
            %distSeg = sum(sum(sum(abs(circleSeg - blueCircle) .^ 2)));
            %if distSeg <= distThresholdBlue
                %blueCircleCount += 1;
                %if distSeg < bestBlueCircleDist
                    %bestBlueCircleDist = distSeg;
                    %bestBlueCirclePos = [r; c];
                %end
            %end

            %% pink circle
            %distSeg = sum(sum(sum(abs(circleSeg - pinkCircle) .^ 2)));
            %if distSeg <= distThresholdPink
                %pinkCircleCount += 1;
                %if distSeg < bestPinkCircleDist
                    %bestPinkCircleDist = distSeg;
                    %bestPinkCirclePos = [r; c];
                %end
            %end

            %% yellow circle
            %distSeg = sum(sum(sum(abs(circleSeg - yellowCircle) .^ 2)));
            %if distSeg <= distThresholdYellow
                %yellowCircleCount += 1;
                %if distSeg < bestYellowCircleDist
                    %bestYellowCircleDist = distSeg;
                    %bestYellowCirclePos = [r; c];
                %end
            %end

            %robotLayoutMin = 0;

            %%for i=1:numRobotLayouts
                %%robotLayoutDist = sum(sum(abs(robotLayoutRot(:, :, :, i) - circleSeg))) 
            %%end

        %end
    %end
    %ImPatch(bestBlueCirclePos(1, 1), bestBlueCirclePos(2, 1), :) = [0, 0, 255];
    %ImPatch(bestYellowCirclePos(1, 1), bestYellowCirclePos(2, 1), :) = [255, 255, 0];
    %ImPatch(bestPinkCirclePos(1, 1), bestPinkCirclePos(2, 1), :) = [255, 0, 150];

    % run the robot detect on the best blue circle
    %rowStart = max(1, bestBlueCirclePos(1, 1) - floor(size(robotLayout, 1) / 2.0))
    %rowEnd = min(size(ImPatch, 1), bestBlueCirclePos(1, 1) + ceil(size(robotLayout, 1) / 2.0) - 1)
    %colStart = max(1, bestBlueCirclePos(2, 1) - floor(size(robotLayout, 2) / 2.0))
    %colEnd = min(size(ImPatch, 2), bestBlueCirclePos(2, 1) + ceil(size(robotLayout, 2) / 2.0) - 1)
    %roboSeg = ImPatch(rowStart:rowEnd, colStart:colEnd, :);
    %figure(j + 2000)
    %imagesc(roboSeg)
    
    %figure(j + 1000)
    %imagesc(ImPatch);

    %robotLayoutMin = 0;
    %robotLayoutDistMin = 10000000;

    %if size(roboSeg) == size(robotLayout)
        %for b=1:numRobotLayouts
            %%figure(30 + b)
            %%sum(sum(abs(robotLayoutsRot(:, :, :, b) - roboSeg)))
            %%imagesc(robotLayoutsRot(:, :, :, b) - roboSeg);
            %robotLayoutDist = sum(sum(sum(abs(robotLayoutsRot(:, :, :, b) - roboSeg))))
            %if robotLayoutDist < robotLayoutDistMin
                %robotLayoutDistMin = robotLayoutDist;
                %robotLayoutMin = b;
            %end
        %end
        
        %figure(j + 3000)
        %robotLayoutMin
        %imagesc(robotLayoutsRot(:, :, :, robotLayoutMin))
    %end

    fprintf('redcircles: %d\n', redCircleCount);
    fprintf('greencircles: %d\n', greenCircleCount);
    fprintf('bluecircles: %d\n', blueCircleCount);
    fprintf('yellowcircles: %d\n\n', yellowCircleCount);
    fprintf('pinkcircles: %d\n\n', pinkCircleCount);
    

end
%sum(patchRGB, 1)
%patchRGB = patchRGB ./ repmat(sum(patchRGB, 1), size(patchRGB, 1), 1)
%patchSizes
%patchRGB = patchRGB ./ repmat(patchSizes, size(patchRGB, 1), 1)
