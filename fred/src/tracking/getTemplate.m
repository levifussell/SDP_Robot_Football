function [template] = getTemplate(image, posR, posC, length)

    posR1 = max(posR - length / 2, 1);
    posR2 = min(posR + length / 2, size(image, 1));
    posC1 = max(posC - length / 2, 1);
    posC2 = min(posC + length / 2, size(image, 2));

    template = image(posR1:posR2, posC1:posC2, :);
end
