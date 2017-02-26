function [TBestLocation, TCount, TBestDist] = findTemplatesInImage2(image, templates, distTemplateThresholds)

    templateNum = size(templates, 4);
    TCount = zeros(templateNum, 1);
    TBestDist = ones(templateNum, 1);
    TBestLocation = ones(templateNum, 2);

    imageD = double(image);

    for t=1:size(templates, 4)
        convTR = conv2(imageD(:, :, 1), templates(:, :, 1, t));
        convTG = conv2(imageD(:, :, 2), templates(:, :, 2, t));
        convTB = conv2(imageD(:, :, 3), templates(:, :, 3, t));
        convT = convTR + convTG + convTB;
        bestV = max(max(convT))
        [bX, bY] = find(convT == max(max(convT)))
        TBestLocation(t, :) = [bX(1), bY(1)]
        TCount(t, :) = sum(sum(convT))
        TBestDist(t, :) = bestV;
    end
end
