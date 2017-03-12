function [TBestLocation, TCount, TBestDist] = findTemplatesInImage(image, templates, distTemplateThresholds)
% NOTE: all templates must be of the same size, otherwise call this function multiple
%  times with each template
    templateRow = size(templates, 1);
    templateCol = size(templates, 2);
    templateNum = size(templates, 4);
    TCount = zeros(templateNum, 1);
    TBestDist = ones(templateNum, 1) .* 100000000; % make this a large number so it isn't the min
    TBestLocation = ones(templateNum, 2);

    imageD = double(image);

    for r=1:(size(image, 1) - templateRow + 1)
        for c=1:(size(image, 2) - templateCol + 1)

            templateSeg = imageD(r:(r + templateRow - 1), c:(c + templateCol - 1), :);

            for t=1:templateNum
                template = templates(:, :, :, t);
                distTemp = sum(sum(sum(abs(templateSeg - template) .^ 2)));
                if distTemp <= distTemplateThresholds(t)
                    TCount(t) += 1;
                    if distTemp < TBestDist(t)
                        TBestDist(t) = distTemp;
                        TBestLocation(t, :) = [r, c];
                    end
                end
            end
        end
    end

end
