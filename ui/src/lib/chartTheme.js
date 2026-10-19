const LIGHT_PALETTE = { text: "#475569", grid: "#e2e8f0" };
const DARK_PALETTE = { text: "#cbd5e1", grid: "#334155" };

/**
 * Builds ApexCharts options for the current colour scheme (text, grid and tooltip colours).
 *
 * @param {boolean} isDark whether the dark theme is active
 * @returns {object} partial ApexCharts options
 */
export function getChartThemeOptions(isDark) {
    var palette = isDark ? DARK_PALETTE : LIGHT_PALETTE;
    return {
        theme: { mode: isDark ? "dark" : "light" },
        chart: { background: "transparent", foreColor: palette.text },
        grid: { borderColor: palette.grid },
        legend: { labels: { colors: palette.text } },
        tooltip: { theme: isDark ? "dark" : "light" },
    };
}

/**
 * Zoom, pan and toolbar settings shared by every graph so they behave consistently.
 *
 * @returns {object} partial ApexCharts options
 */
export function getZoomOptions() {
    return {
        chart: {
            zoom: { enabled: true, type: "x", autoScaleYaxis: true },
            toolbar: {
                show: true,
                autoSelected: "zoom",
                tools: {
                    download: false,
                    selection: false,
                    zoom: true,
                    zoomin: true,
                    zoomout: true,
                    pan: true,
                    reset: true,
                },
            },
        },
    };
}

/**
 * Applies the shared theme and zoom settings on top of chart specific options.
 * Sections defined by the chart (for example its custom tooltip) are preserved.
 *
 * @param {object} options the chart specific options
 * @param {boolean} isDark whether the dark theme is active
 * @returns {object} the merged options
 */
export function withChartDefaults(options, isDark) {
    var themeOptions = getChartThemeOptions(isDark);
    var zoomOptions = getZoomOptions();
    return {
        ...options,
        theme: { ...themeOptions.theme, ...options.theme },
        chart: { ...options.chart, ...themeOptions.chart, ...zoomOptions.chart },
        grid: { ...themeOptions.grid, ...options.grid },
        legend: { ...options.legend, labels: { ...themeOptions.legend.labels, ...options.legend?.labels } },
        tooltip: { ...options.tooltip, ...themeOptions.tooltip },
    };
}
