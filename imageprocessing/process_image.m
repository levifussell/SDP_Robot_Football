function [M, patchCorners] = process_image(m, debug)

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
    activeThreshold = 900;
    m_var_half_max_active = find_maxs(m_var_half_max, activeThreshold);


    % calculate the areas positions
    % (to do this we upscale the positions via the max-pool and centre them, keeping
    %  the low-res image 1:4)
    [active_patches_row, active_patches_col] = find(m_var_half_max_active);
    active_patches_row_scaled = active_patches_row .* max_pool_size;
    active_patches_col_scaled = active_patches_col .* max_pool_size;
    active_patches_row_centred = active_patches_row_scaled .- (max_pool_size / 2);
    active_patches_col_centred = active_patches_col_scaled .- (max_pool_size / 2);
    
    % look 1 pixel around the active areas on the smaller images
    %sizeOfAreaAroundPatch = 1;
    patchCorners = zeros(2, 2, size(active_patches_row));
    for i=1:size(active_patches_row)
        up = max(active_patches_row(i) - 1, 1);
        down = min(active_patches_row(i) + 1, size(m_var_half_max_active, 1));
        left = max(active_patches_col(i) - 1, 1);
        right = min(active_patches_col(i) + 1, size(m_var_half_max_active, 2));

        % assign sides of the patches
        m_var_half_max_active(down, active_patches_col(i)) = 10000;
        m_var_half_max_active(up, active_patches_col(i)) = 10000;
        m_var_half_max_active(active_patches_row(i), right) = 10000;
        m_var_half_max_active(active_patches_row(i), left) = 10000;
        
        % asssign corners of the patches
        m_var_half_max_active(down, left) = 10000;
        m_var_half_max_active(down, right) = 10000;
        m_var_half_max_active(up, left) = 10000;
        m_var_half_max_active(up, right) = 10000;

        % convert patch corners to points
        patchCorners(1, 1, i) =  down .* max_pool_size .+ (max_pool_size / 2) .- 1;
        patchCorners(1, 2, i) =  left .* max_pool_size .- (max_pool_size) .+ 1;
        patchCorners(2, 1, i) =  right .* max_pool_size .+ (max_pool_size / 2) .- 1;
        patchCorners(2, 2, i) =  up .* max_pool_size .- (max_pool_size) .+ 1;
    end

    %[active_patches_row, active_patches_col] = find(m_var_half_max_active);
    %active_patches_row_scaled = active_patches_row .* max_pool_size;
    %active_patches_col_scaled = active_patches_col .* max_pool_size;
    %active_patches_row_centred = active_patches_row_scaled .- (max_pool_size / 2);
    %active_patches_col_centred = active_patches_col_scaled .- (max_pool_size / 2);

    if debug
        figure(4)
        colormap(gray)
        imagesc(m_var_half_max_active)
    end

    % scale the image back to its normal size
    m_scale = scale_image(m_var_half_max_active, m_var_half);
    size(m_scale)
    size(m_var_half)
    diffSize = size(m_var_half) - size(m_scale);
    edgeB = floor(diffSize ./ 1)
    cut_dim1 = size(m_scale, 1) + edgeB(1) - 1
    cut_dim2 = size(m_scale, 2) + edgeB(2) - 1
    m_overlay = repmat(m_scale(1:(cut_dim1 - edgeB(1) + 1), 1:(cut_dim2 - edgeB(2) + 1), :) > 0, [1, 1, 3]) .* m_half(edgeB(1):cut_dim1, edgeB(2):cut_dim2, :);

    % add the active patches to the final image
    for i=1:size(active_patches_row_centred)
        %m_overlay(active_patches_row_centred(i), active_patches_col_centred(i), :) = [255, 255, 255];
        drawCornerMarkers = true;
        if drawCornerMarkers
            m_overlay(patchCorners(1, 1, i), patchCorners(1, 2, i), :) = [255, 255, 255];
            m_overlay(patchCorners(1, 1, i), patchCorners(2, 1, i), :) = [255, 255, 255];
            m_overlay(patchCorners(2, 2, i), patchCorners(1, 2, i), :) = [255, 255, 255];
            m_overlay(patchCorners(2, 2, i), patchCorners(2, 1, i), :) = [255, 255, 255];
        end
    end

    if debug
        figure(5)
        imagesc(m_overlay)
    end


    % return the processed image
    M = m_overlay;

end
