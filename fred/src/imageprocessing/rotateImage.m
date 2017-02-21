function [newImage] = rotateImage(m, degree)

    rad = double(degree) ./ 180 .* 3.1415;
    % 2x2, 2D rotation matrix
    rotMatrix = [cos(rad), sin(rad); -sin(rad), cos(rad)];

    newImage = zeros(size(m));

    for r=1:size(m, 1)
        for c=1:size(m, 2)
            
            % centre the origin
            pixelPos = [r - size(m, 1) / 2.0; c - size(m, 2) / 2.0];
            pixelPosRot = floor(rotMatrix * pixelPos);
            pixelPosRot(1, 1) += ceil(size(m, 1) / 2);
            pixelPosRot(2, 1) += ceil(size(m, 2) / 2);
            
            if pixelPosRot(1, 1) > 0 && pixelPosRot(1, 1) < size(m, 1) && pixelPosRot(2, 1) > 0 && pixelPosRot(2, 1) < size(m, 2)
                newImage(pixelPosRot(1, 1), pixelPosRot(2, 1), :) = m(r, c, :);
            end
        end
    end

end
