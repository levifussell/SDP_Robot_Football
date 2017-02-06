I1 = imread("imgs/snap-unknown-20170126-134652-1.jpeg");
%I1 = imread("imgs/snap-unknown-20170126-134705-1.jpeg");

[I1p, patches] = process_image(I1, false);
figure(20)
imagesc(I1p)
%patches
patchRGB = zeros(3, size(patches, 3));
patchSizes = zeros(1, size(patches, 3));
% draw each patch
for j=1:size(patches, 3)
    %imagesc(I1p(patches(2, 2, i):patches(1, 1, i), patches(1, 2, i):patches(2, 1, i), :))
    ImPatch = I1p(patches(2, 2, j):patches(1, 1, j), patches(1, 2, j):patches(2, 1, j), :);
    %figure(j + 1000)
    %imagesc(ImPatch);
    %ImPatch = ImPatch .* repmat(var(ImPatch, 1, 3) > 2000, [1, 1, 3]);
    patchRGB(1, i) = sum(sum(ImPatch(:, :, 1)));
    patchRGB(2, i) = sum(sum(ImPatch(:, :, 2)));
    patchRGB(3, i) = sum(sum(ImPatch(:, :, 3)));
    patchSizes(i) = size(ImPatch, 1) .* size(ImPatch, 2);

    % run each image patch through a circle search
    % red circle
    redCircle = double(zeros(3, 3, 3));
    redCircle(:, :, 1) += 255.0;
    greenCircle = double(zeros(3, 3, 3));
    greenCircle(:, :, 2) += 255.0;
    % blue circles have a slight green edge
    %  (because they are in the middle of the robot)
    blueCircle = zeros(3, 3, 3);
    blueCircle(:, :, 2) = [255 255 255; 255 0 255; 255 0 255];
    blueCircle(:, :, 3) = [0 0 0; 0 255 0; 0 255 0];
    yellowCircle = double(zeros(3, 3, 3));
    % yellow circles have a slight green edge
    %  (because they are in the middle of the robot)
    yellowCircle(:, :, 1) = [0 0 0; 0 255 255; 0 255 255];
    yellowCircle(:, :, 2) += 255.0;

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
    end

    distThresholdRed = 1400;
    distThresholdGreen = 2000;
    distThresholdBlue = 2200;
    distThresholdYellow = 2100;
    redCircleCount = 0;
    greenCircleCount = 0;
    yellowCircleCount = 0;
    blueCircleCount = 0;
    ImPatchD = double(ImPatch);

    bestBlueCirclePos = [1; 1];
    bestBlueCircleDist = 1000000;
    bestYellowCirclePos = [1, 1];
    bestYellowCircleDist = 1000000;

    for r=1:(size(ImPatch, 1) - size(redCircle, 1))
        for c = 1:(size(ImPatch, 2) - size(redCircle, 2))
            circleSeg = ImPatchD(r:(r + size(redCircle, 1) - 1), c:(c + size(redCircle, 2) - 1), :);


            % red circle
            distSeg = sum(sum(sum(abs(circleSeg - redCircle))));
            if distSeg <= distThresholdRed
                redCircleCount += 1;
            end

            % green circle
            distSeg = sum(sum(sum(abs(circleSeg - greenCircle))));
            if distSeg <= distThresholdGreen
                greenCircleCount += 1;
            end

            % blue circle
            distSeg = sum(sum(sum(abs(circleSeg - blueCircle))));
            if distSeg <= distThresholdBlue
                blueCircleCount += 1;
                if distSeg < bestBlueCircleDist
                    bestBlueCircleDist = distSeg;
                    bestBlueCirclePos = [r; c];
                end
            end

            % yellow circle
            distSeg = sum(sum(sum(abs(circleSeg - yellowCircle))));
            if distSeg <= distThresholdYellow
                yellowCircleCount += 1;
                if distSeg < bestYellowCircleDist
                    bestYellowCircleDist = distSeg;
                    bestYellowCirclePos = [r; c];
                end
            end

            robotLayoutMin = 0;

            %for i=1:numRobotLayouts
                %robotLayoutDist = sum(sum(abs(robotLayoutRot(:, :, :, i) - circleSeg))) 
            %end

        end
    end
    ImPatch(bestBlueCirclePos(1, 1), bestBlueCirclePos(2, 1), :) = [255, 255, 255];

    figure(j + 1000)
    imagesc(ImPatch);

    %fprintf('redcircles: %d\n', redCircleCount);
    %fprintf('greencircles: %d\n', greenCircleCount);
    fprintf('bluecircles: %d\n', blueCircleCount);
    fprintf('yellowcircles: %d\n\n', yellowCircleCount);
    

end
%sum(patchRGB, 1)
%patchRGB = patchRGB ./ repmat(sum(patchRGB, 1), size(patchRGB, 1), 1)
%patchSizes
%patchRGB = patchRGB ./ repmat(patchSizes, size(patchRGB, 1), 1)
