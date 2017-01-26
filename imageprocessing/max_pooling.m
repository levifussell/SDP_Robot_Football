function [M] = max_pooling(m, m_size)
% given an image m and a size of the pool, perform max pooling wit step
% size equal to the size of the pool over the image. This will reduce the
% images size by the size of the pool (note: if image dimensions and pool
% size are not divisible, some image data will be lost)

    % set the step rate for rows and columns
    pool_r = floor(size(m, 1) / m_size);
    pool_c = floor(size(m, 2) / m_size);

    % create the new image matrix at the expected size
    M = double(zeros(pool_r, pool_c));

    for r=1:pool_r
        for c=1:pool_c
            % get the starting index of the pool at (r, c)
            r_ind = (r - 1) .* m_size + 1;
            c_ind = (c - 1) .* m_size + 1;
            % extract the m_size x m_size matrix from the image
            overM = zeros(size(m));
            overM(r_ind:(r_ind+m_size - 1), c_ind:(c_ind+m_size - 1)) = double(ones(m_size, m_size));
            pooled_out_m = overM .* double(m);
            % find the extracted matrix's max value and assign it to the
            % new image
            M(r, c) = max(max(pooled_out_m));
        end
    end
end