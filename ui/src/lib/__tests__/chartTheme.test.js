import assert from "node:assert/strict";
import { describe, it } from "node:test";
import { getChartThemeOptions, getZoomOptions, withChartDefaults } from "../chartTheme.js";

describe("chartTheme.getChartThemeOptions", () => {
    it("uses the dark mode and a light text colour when dark", () => {
        var options = getChartThemeOptions(true);

        assert.equal(options.theme.mode, "dark");
        assert.equal(options.tooltip.theme, "dark");
        assert.equal(options.chart.foreColor, "#cbd5e1");
    });

    it("uses the light mode and a dark text colour when light", () => {
        var options = getChartThemeOptions(false);

        assert.equal(options.theme.mode, "light");
        assert.equal(options.tooltip.theme, "light");
        assert.equal(options.chart.foreColor, "#475569");
    });
});

describe("chartTheme.getZoomOptions", () => {
    it("enables zoom, pan and the toolbar", () => {
        var chart = getZoomOptions().chart;

        assert.equal(chart.zoom.enabled, true);
        assert.equal(chart.toolbar.show, true);
        assert.equal(chart.toolbar.tools.pan, true);
        assert.equal(chart.toolbar.tools.zoomin, true);
    });
});

describe("chartTheme.withChartDefaults", () => {
    it("keeps chart specific settings while forcing zoom and theme", () => {
        var custom = () => "tooltip";
        var options = withChartDefaults(
            {
                chart: { type: "line", height: 250, zoom: { enabled: false }, toolbar: { show: false } },
                tooltip: { enabled: true, custom: custom },
                legend: { show: true },
            },
            true
        );

        assert.equal(options.chart.type, "line");
        assert.equal(options.chart.height, 250);
        assert.equal(options.chart.zoom.enabled, true);
        assert.equal(options.chart.toolbar.show, true);
        assert.equal(options.tooltip.custom, custom);
        assert.equal(options.tooltip.theme, "dark");
        assert.equal(options.legend.show, true);
    });
});
