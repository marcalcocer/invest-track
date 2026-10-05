import assert from "node:assert/strict";
import { describe, it } from "node:test";
import { getTodayAnnotation } from "../ChartUtils.js";

describe("ChartUtils.getTodayAnnotation", () => {
    it("draws a single dashed vertical line at the current date", () => {
        const before = Date.now();
        const annotation = getTodayAnnotation();
        const after = Date.now();

        assert.equal(annotation.xaxis.length, 1);
        assert.ok(annotation.xaxis[0].x >= before);
        assert.ok(annotation.xaxis[0].x <= after);
        assert.equal(annotation.xaxis[0].strokeDashArray, 4);
        assert.equal(annotation.xaxis[0].label.text, "Today");
    });

    it("accepts a custom label", () => {
        const annotation = getTodayAnnotation("Hoy");

        assert.equal(annotation.xaxis[0].label.text, "Hoy");
    });
});