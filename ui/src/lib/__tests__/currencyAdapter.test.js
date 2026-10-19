import assert from "node:assert/strict";
import { describe, it } from "node:test";
import { formatPercent, formatValueForPrivacy } from "../currencyAdapter.js";

describe("currencyAdapter.formatPercent", () => {
    it("formats a positive ratio as a percentage", () => {
        var result = formatPercent(0.1234);
        assert.equal(result, "12.34%");
    });

    it("formats a negative ratio as a percentage", () => {
        var result = formatPercent(-0.05);
        assert.equal(result, "-5.00%");
    });

    it("formats zero as 0.00%", () => {
        var result = formatPercent(0);
        assert.equal(result, "0.00%");
    });
});

describe("currencyAdapter.formatValueForPrivacy", () => {
    it("masks the monetary value when privacy mode is on", () => {
        var result = formatValueForPrivacy(1234.5, "EUR", true);
        assert.equal(result, "******");
    });

    it("formats the monetary value when privacy mode is off", () => {
        var result = formatValueForPrivacy(0, "EUR", false);
        assert.match(result, /0,00/);
    });

    it("keeps the percentage visible while the monetary value is masked", () => {
        var money = formatValueForPrivacy(1234.5, "EUR", true);
        var percent = formatPercent(0.1234);

        assert.equal(money, "******");
        assert.equal(percent, "12.34%");
    });
});
