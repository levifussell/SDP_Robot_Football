function [TBestLocation, TCount, TBestDist, TBestRot] = matchTemplateSet(image, templateSet, posR, posC, assessRegion)
    
    TBestLocation = [-10000, -10000];
    TCount = -10000;
    TBestDist = -10000;
    for i=1:size(templateSet, 4)
        [tB, tC, tD] = matchTemplate(image, templateSet(:, :, :, i), posR, posC, assessRegion);
        if tD > TBestDist
            TBestDist = tD;
            TBestLocation = tB;
            TCount = tC;
            TBestRot = 360.0 ./ size(templateSet, 4) .* i;
        end
    end

end
