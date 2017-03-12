
function [image_scaled, target_scaled] = scale_image(m_image, m_image_target)

    edge_loss = mod(size(m_image_target), size(m_image));
    scale_v = floor(size(m_image_target) ./ size(m_image));

    image_scaled = kron(m_image, ones(scale_v));
        
    target_scaled = m_image_target(1:(size(m_image_target, 1) - edge_loss(1)), 1:(size(m_image_target, 2) - edge_loss(2)));

    % loss_of_pixels = size(image_expand_tiled) - size(m_image_target);
    % border_loss_l = ceil(abs(loss_of_pixels / 2));
    % border_loss_r = floor(abs(loss_of_pixels / 2));
    % image_scaled = image_expand_tiled(border_loss_l(1):(size(image_expand_tiled, 1) - border_loss_r(1) - 1), 
    %                                 border_loss_l(2):(size(image_expand_tiled, 2) - border_loss_r(2) - 1));

    end
