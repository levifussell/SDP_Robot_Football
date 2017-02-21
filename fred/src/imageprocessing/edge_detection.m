function [image_edge] = edge_detection(image_m, gauss_window, break_points)
% uses thresholds and convolution matrices to segment an image into the
% most significant buckets. This function does not make the image binary,
% but is a pre-processing step for doing so; instead it reduces the
% complexity of the image and helps for background removal

    if(nargin < 3)
        break_points = false;
    end

    % create a bin of rgb byte values
    edges = transpose(1:255);

    [rows, columns] = size(image_m);
    Ig_vec = reshape(image_m, 1, rows * columns);

    % NOTE: use histc for Octave and histcounts for Matlab
    I_hist = histc(Ig_vec, edges);

    % smooth the histogram with a gaussian
    filter = gauss_window;
    filter = filter/sum(filter);
    smooth_hist = conv(filter, I_hist);
    
    % find minimums between thresholds
    offset_error = 0;
    a = smooth_hist < ([10, smooth_hist(:, 1:(size(smooth_hist, 2) - 1))] - repmat(offset_error, 1, size(smooth_hist, 2)));
    b = smooth_hist < ([smooth_hist(:, 2:size(smooth_hist, 2)), 10] - repmat(offset_error, 1, size(smooth_hist, 2)));
    hist_maximums = a .* b .* smooth_hist;

    if break_points
        figure(100)
        plot(smooth_hist)
        hold on
        hist_maximums = hist_maximums + (hist_maximums <= 0) * -400000;
        scatter([1:size(hist_maximums, 2)], hist_maximums);
        input('drawing histogram and minimums. continue?');
    end

    % create a set of thresholds
    I_thresholds = find(hist_maximums > 0);
    I_num_thresholds = size(I_thresholds, 2);
    threshold_rate = 255 ./ I_num_thresholds;
    Ig_cluster = zeros(size(image_m));

    % for each threshold, cluster the colours of the image into distinct
    % bins
    for i=1:I_num_thresholds

        Ig_cluster = Ig_cluster + (image_m < I_thresholds(:, i)) .* threshold_rate;
    end

    image_edge = Ig_cluster;
end