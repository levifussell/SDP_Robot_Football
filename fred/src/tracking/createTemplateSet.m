function [templateSet] = createTemplateSet(template, setSize)
    
    templateSet = zeros(size(template, 1), size(template, 2), size(template, 3), setSize);
    rateRot = 360.0 ./ setSize;
    for i=1:setSize
        rateRot
        templateSet(:, :, :, i) = uint8(rotateImage(template, rateRot * i));
        %figure(88 * i)
        %imagesc(uint8(templateSet(:, :, :, i)))
    end
end
