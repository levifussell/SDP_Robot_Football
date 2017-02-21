function [maxs] = find_maxs(m_data, maxDist)

    shiftCount = 2;
    a_x = (m_data - ([ones(size(m_data, 1), 1) .* 1, m_data(:, 1:(size(m_data, 2) - 1))])) > maxDist;
    b_x = (m_data - ([m_data(:, 2:size(m_data, 2)), ones(size(m_data, 1), 1) .* 1])) > maxDist;
    a_y = (m_data - ([ones(1, size(m_data, 2)) .* 1; m_data(1:(size(m_data, 1) - 1), :)])) > maxDist;
    b_y = (m_data - ([m_data(2:size(m_data, 1), :); ones(1, size(m_data, 2)) .* 1])) > maxDist;
    maxs = ((a_x .* b_x .+ a_y .* b_y) > 0) .* m_data;

end
