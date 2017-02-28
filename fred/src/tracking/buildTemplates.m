function [templates] = buildTemplates(images, position, radius)

    template = zeros(size(image));

    for i=1:size(images, 4)
        template += getTemplate(images(:, :, :, i), position(1), position(2), radius);
    end

    template = template ./ size(images, 4);

    templates = createTemplateSet(template, 20);
end
