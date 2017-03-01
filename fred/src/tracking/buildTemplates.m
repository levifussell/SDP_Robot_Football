function [templates] = buildTemplates(imageAvg, position, radius)

    %template = zeros(size(image));
    figure(10011)
    imagesc(floor(imageAvg))
    %for i=1:size(images, 4)
    template = getTemplate(imageAvg, position(1), position(2), radius);
    figure(23232)
    imagesc(floor(template))
    %end

    %template = template ./ size(images, 4);

    templates = createTemplateSet(template, 8);
end
