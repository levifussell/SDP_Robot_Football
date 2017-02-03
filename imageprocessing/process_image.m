function [M] = process_image(m, debug)

    % half the size of the image (could even try a fourth)
    %m_var_half = m_var(1:4:end, 1:4:end);
    m_half = m(1:4:end, 1:4:end, :);

    if debug
        figure(1)
        colormap(gray)
        imagesc(m_half)
    end

    % compute the variance of the 3 colour channels
    m_var_half = var(m_half, 0, 3);

    if debug
        figure(2)
        colormap(gray)
        imagesc(m_var_half)
    end

    % perform max-pooling on the image (for now we choose 15x15 pooling)
    max_pool_size = 10;
    m_var_half_max = max_pooling(m_var_half, max_pool_size);

    if debug
        figure(3)
        colormap(gray)
        imagesc(m_var_half_max)
    end

    % find the areas of interest
    m_var_half_max_active = find_maxs(m_var_half_max, 1000);

    if debug
        figure(4)
        colormap(gray)
        imagesc(m_var_half_max_active)
    end

    % calculate the areas positions
    % (to do this we upscale the positions via the max-pool and centre them, keeping
    %  the low-res image 1:4)
    [active_patches_row, active_patches_col] = find(m_var_half_max_active)
    active_patches_row_scaled = active_patches_row .* max_pool_size;
    active_patches_col_scaled = active_patches_col .* max_pool_size;
    active_patches_row_centred = active_patches_row_scaled .- (max_pool_size / 2);
    active_patches_col_centred = active_patches_col_scaled .- (max_pool_size / 2);
    
    % scale the image back to its normal size
    m_scale = scale_image(m_var_half_max_active, m_var_half);
    m_overlay = repmat(m_scale > 0, [1, 1, 3]) .* m_half(1:size(m_scale, 1), 1:size(m_scale, 2), :);

    % add the active patches to the final image
    for i=1:size(active_patches_row_centred)
        m_overlay(active_patches_row_centred(i), active_patches_col_centred(i), :) = [255, 255, 255];
    end

    if debug
        figure(5)
        imagesc(m_overlay)
    end


    % return the processed image
    M = m_overlay;

end
