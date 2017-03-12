function [robotsPos, robotsAngle, robotsColour, patches] = runVision(imageData, activeThresh, redThresh, blueThresh, yellowThresh, greenThresh, pinkThresh)

	downSample = 4;
	cropEdge = 30;
	height = 480;
	width = 640;
    I1 = imageData(cropEdge:(height - cropEdge), cropEdge:(width - cropEdge), :);
    [I1p, patches] = process_image(I1, activeThresh, true);
    %figure(20)
    %imagesc(I1p)
    %patches
    patchRGB = zeros(3, size(patches, 3));
    patchSizes = zeros(1, size(patches, 3));

    robotsPos = zeros(size(patches, 3), 2);
    robotsAngle = zeros(size(patches, 3), 1);
    robotsColour = zeros(size(patches, 3), 1);

    % draw each patch

    for j=1:size(patches, 3)

        %imagesc(I1p(patches(2, 2, i):patches(1, 1, i), patches(1, 2, i):patches(2, 1, i), :))
        ImPatchTopLeft = [patches(2, 2, j), patches(1, 2, j)];
        ImPatch = I1p(patches(2, 2, j):patches(1, 1, j), patches(1, 2, j):patches(2, 1, j), :);
        figure(j + 1000)
        imagesc(ImPatch);
        ImPatch = ImPatch .* repmat(var(ImPatch, 1, 3) > 2000, [1, 1, 3]);
        patchRGB(1, j) = sum(sum(ImPatch(:, :, 1)));
        patchRGB(2, j) = sum(sum(ImPatch(:, :, 2)));
        patchRGB(3, j) = sum(sum(ImPatch(:, :, 3)));
        patchSizes(j) = size(ImPatch, 1) .* size(ImPatch, 2);

        % robot layout
        robotLayout = double(zeros(10, 10, 3));

        distThresholdRed = redThresh;%400000;
        distThresholdGreen = greenThresh;%400000;
        distThresholdBlue = blueThresh;%400000;
        distThresholdYellow = yellowThresh;%300000;
        distThresholdPink = pinkThresh;%400000;
        ImPatchD = double(ImPatch);

        [robotPos, robotAngle, robotColour] = findRobot(ImPatch, distThresholdBlue, distThresholdYellow, distThresholdPink, distThresholdGreen, true, j * 13);
	robotPos = ones(1, 2);
	robotAngle = ones(1, 1);
	robotColour = ones(1, 1);
	%scale robot position back up from 1:4

	figure(101 * j)
	ImPatch(robotPos(1, 1), robotPos(1, 2), :) = [0, 255, 0]; 
	imagesc(ImPatch)

        robotPosFinal = ImPatchTopLeft + robotPos;
        fprintf('robot: %d, %d\n', robotPosFinal, robotPosFinal);
        fprintf('\t, %f\n', robotAngle);
        fprintf('\t, %d\n', robotColour(1, 1));

	I1p(robotPosFinal(1, 1), robotPosFinal(1, 2), :) = [0, 0, 255];

	robotPosFinal = (robotPosFinal .* downSample) + cropEdge
        robotsPos(j, :) = robotPosFinal;
        robotsAngle(j, 1) = robotAngle;
        robotsColour(j, 1) = robotColour(1, 1);

	
	I1(robotPosFinal(1, 1) - cropEdge, robotPosFinal(1, 2) - cropEdge, :) = [0, 0, 255];
    end

	% scale patches by 1;4
	patches = (patches .* downSample) + cropEdge;

	figure(1314141)
	imagesc(I1p)
	figure(1314161)
	imagesc(I1)
end
