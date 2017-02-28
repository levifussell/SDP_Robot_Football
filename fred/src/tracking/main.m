I1 = imread("imgs/saved.png");
%we only pass in images scaled by 1:4
I1_scale = I1(1:4:end, 1:4:end, :);

pos = [28 24];

templates = buildTemplates(I1_scale, pos, 10);

[robotPos, robotAngle] = runTracker(I1_scale, pos, templates, 20)

figure(101)
imagesc(I1_scale)
 %state 2
%I2 = imread("imgs/saved2.png");
%we only pass in images scaled by 1:4
%I2_scale = I2(1:4:end, 1:4:end, :);

%previousPosR = 28;
%previousPosC = 24;

%tD = time();
%Temp1 = getTemplate(I1_scale, previousPosR, previousPosC, 10);
%disp("time to get template: "), disp(time() - tD)
%tD = time();

%TempSet = createTemplateSet(Temp1, 20);
%disp("time to create template set "), disp(time() - tD)
%tD = time();

%Temp1_rot = uint8(rotateImage(Temp1, 10.0));
%[bestLoc, TCount, TBestDist, TBestRot] = matchTemplateSet(I2_scale, TempSet, previousPosR, previousPosC, 20);
%bestLoc
%I1_scale_t = I2_scale;
%I1_scale_t(bestLoc(1), bestLoc(2), :) = [0, 255, 0];
%disp("time to match template: "), disp(time() - tD)
%tD = time();

%disp("rot of robot: "), disp(TBestRot)

%figure(101)
%imagesc(Temp1)

%figure(1011)
%imagesc(uint8(rotateImage(Temp1, 10.0)))

%figure(104)
%imagesc(I1_scale_t)

%figure(100)
%imagesc(I1_scale)
