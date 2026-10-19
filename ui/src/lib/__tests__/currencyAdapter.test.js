import assert from "node:assert/strict";
import { describe, it } from "node:test";
import { formatPercentForPrivacy } from "../currencyAdapter.js";

describe("currencyAdapter.formatPercentForPrivacy", () => {
    it("masks the value when privacy mode is on", () => {
        var result = formatPercentForPrivacy(0.1234, true);
        assert.equal(result, "******");
    });

    it("formats a positive ratio as a percentage when privacy mode is off", () => {
        var result = formatPercentForPrivacy(0.1234, false);
        assert.equal(result, "12.34%");
    });

    it("formats a negative ratio as a percentage when privacy mode is off", () => {
        var result = formatPercentForPrivacy(-0.05, false);
        assert.equal(result, "-5.00%");
    });

    it("formats zero as 0.00%", () => {
        var result = formatPercentForPrivacy(0, false);
        assert.equal(result, "0.00%");
    });
});
