function [TBestLocation, TCount, TBestDist] = matchTemplate(image, template, posR, posC, assessRegion)

    TCount = zeros(1, 1);
    TBestDist = ones(1, 1);
    TBestLocation = ones(1, 2);
    %imageD = double(image);
    imageD = getTemplate(image, posR, posC, assessRegion);

    convT = convn(imageD, template(end:-1:1, end:-1:1, end:-1:1), "valid");
    templateSize = size(template);
    bestV = max(convT(:));
    [bX, bY] = find(convT ==  bestV);

    %bX += (size(template, 1) / 2);
    %bX
    %bY
    %size(template)
    %bY += (size(template, 2) / 2);
    %imageD(bX, bY, :) = [255, 0, 0];
    %figure(floor(1212 * rand()))
    %imagesc(imageD)

    bX = floor(max(1, min(size(image, 1), bX + posR - assessRegion / 2)));
    bY = floor(max(1, min(size(image, 2), bY + posC - assessRegion / 2)));
    TBestLocation(:) = [bX(1), bY(1)];
    TCount(:) = sum(convT(:));
    TBestDist(:) = bestV;

end
