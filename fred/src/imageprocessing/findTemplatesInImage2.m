function [TBestLocation, TCount, TBestDist] = findTemplatesInImage2(image, templates, distTemplateThresholds)

    templateNum = size(templates, 4);
    TCount = zeros(templateNum, 1);
    TBestDist = ones(templateNum, 1);
    TBestLocation = ones(templateNum, 2);

    imageD = double(image);

    for t=1:size(templates, 4)
        %convTR = conv2(imageD(:, :, 1), templates(:, :, 1, t), "valid");
        %convTG = conv2(imageD(:, :, 2), templates(:, :, 2, t), "valid");
        %convTB = conv2(imageD(:, :, 3), templates(:, :, 3, t), "valid");
        %convT = convTR + convTG + convTB;blueThreshold, yellowThreshold];
        convT = convn(imageD, templates(:, :, :, t)(end:-1:1, end:-1:1, end:-1:1), "valid");
        templateSize = size(templates(:, :, :, t));
        bestV = max(max(convT));
        [bX, bY] = find(convT == max(convT(:)));
        %TBestLocation(t, :) = [bX(1) - (templateSize(1) - 1) / 2, bY(1) - (templateSize(2) - 1) / 2]
        TBestLocation(t, :) = [bX(1), bY(1)];
        TCount(t, :) = sum(convT(:))
        TBestDist(t, :) = bestV;
    end
end
