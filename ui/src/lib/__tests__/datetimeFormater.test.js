import assert from "node:assert/strict";
import { describe, it } from "node:test";
import { formatDate, formatDatetime } from "../datetimeFormater.js";

describe("datetimeFormater.formatDate", () => {
    it("zero-pads a single-digit day and month", () => {
        const result = formatDate(new Date(2024, 0, 5, 3, 7));

        assert.equal(result, "05-01-2024");
    });

    it("keeps a two-digit day and month unchanged", () => {
        const result = formatDate(new Date(2023, 10, 25, 12, 30));

        assert.equal(result, "25-11-2023");
    });

    it("accepts an ISO string input", () => {
        const result = formatDate(localIso(2024, 2, 9, 8, 15));

        assert.equal(result, "09-03-2024");
    });
});

describe("datetimeFormater.formatDatetime", () => {
    it("zero-pads day, month, hours and minutes", () => {
        const result = formatDatetime(new Date(2024, 0, 5, 3, 7));

        assert.equal(result, "05-01-2024 03:07");
    });

    it("keeps two-digit values unchanged", () => {
        const result = formatDatetime(new Date(2023, 10, 25, 18, 45));

        assert.equal(result, "25-11-2023 18:45");
    });

    it("accepts an ISO string input", () => {
        const result = formatDatetime(localIso(2024, 2, 9, 8, 15));

        assert.equal(result, "09-03-2024 08:15");
    });
});

function localIso(year, monthIndex, day, hours, minutes) {
    return new Date(year, monthIndex, day, hours, minutes).toISOString();
}
