import assert from "node:assert/strict";
import { describe, it } from "node:test";
import { ForecastUtils } from "../ForecastUtils.js";

/** Local midnight, so the fixtures do not depend on the timezone of the machine. */
function localDate(year, month, day) {
    return new Date(year, month, day);
}

function entry(overrides = {}) {
    return {
        id: 1,
        datetime: localDate(2026, 1, 28).toISOString(),
        initialInvestedAmount: 1000,
        reinvestedAmount: 0,
        totalInvestedAmount: 1000,
        profitability: 0.2,
        obtained: 1200,
        benefit: 200,
        ...overrides
    };
}

function forecast(overrides = {}) {
    return {
        id: 1,
        name: "Test forecast",
        startDate: "2026-01-01",
        endDate: "2026-01-31",
        scenarioRates: { PESSIMIST: 0.287, NEUTRAL: 0.471, OPTIMIST: 0.604 },
        monthlyContribution: 0,
        ...overrides
    };
}

describe("ForecastUtils.addMonths", () => {
    it("adds calendar months to an ISO date", () => {
        assert.equal(ForecastUtils.addMonths("2026-02-23", 12), "2027-02-23");
        assert.equal(ForecastUtils.addMonths("2026-01-01", 11), "2026-12-01");
        assert.equal(ForecastUtils.addMonths("2026-01-01", 0), "2026-01-01");
    });

    it("clamps to the end of the month instead of overflowing", () => {
        assert.equal(ForecastUtils.addMonths("2026-01-31", 1), "2026-02-28");
        assert.equal(ForecastUtils.addMonths("2024-01-31", 1), "2024-02-29");
        assert.equal(ForecastUtils.addMonths("2026-03-31", -1), "2026-02-28");
    });

    it("crosses year boundaries", () => {
        assert.equal(ForecastUtils.addMonths("2026-11-30", 3), "2027-02-28");
        assert.equal(ForecastUtils.addMonths("2026-02-23", -2), "2025-12-23");
    });

    it("returns null for an invalid date", () => {
        assert.equal(ForecastUtils.addMonths("", 12), null);
        assert.equal(ForecastUtils.addMonths("not-a-date", 12), null);
    });
});

describe("ForecastUtils.monthsBetween", () => {
    it("returns the whole months between two ISO dates", () => {
        assert.equal(ForecastUtils.monthsBetween("2026-02-23", "2027-02-23"), 12);
        assert.equal(ForecastUtils.monthsBetween("2026-01-01", "2026-12-01"), 11);
        assert.equal(ForecastUtils.monthsBetween("2026-01-01", "2026-01-01"), 1);
    });

    it("never returns less than one month", () => {
        assert.equal(ForecastUtils.monthsBetween("2027-02-23", "2026-02-23"), 1);
    });

    it("defaults to one month when a date is missing", () => {
        assert.equal(ForecastUtils.monthsBetween("2026-01-01", undefined), 1);
        assert.equal(ForecastUtils.monthsBetween(undefined, undefined), 1);
    });

    it("round trips with addMonths, so editing a forecast keeps its duration", () => {
        const start = "2026-02-23";

        for (const months of [1, 6, 12, 24]) {
            const end = ForecastUtils.addMonths(start, months);
            assert.equal(ForecastUtils.monthsBetween(start, end), months);
        }
    });
});

describe("ForecastUtils.findNearestEntry", () => {
    it("returns null when there are no entries", () => {
        assert.equal(ForecastUtils.findNearestEntry("2026-01-01", []), null);
        assert.equal(ForecastUtils.findNearestEntry("2026-01-01", null), null);
    });

    it("returns the entry closest to the target date", () => {
        const entries = [
            entry({ id: 1, datetime: localDate(2025, 0, 10).toISOString() }),
            entry({ id: 2, datetime: localDate(2025, 5, 15).toISOString() }),
            entry({ id: 3, datetime: localDate(2025, 11, 20).toISOString() })
        ];

        const nearest = ForecastUtils.findNearestEntry("2025-06-01", entries);

        assert.equal(nearest.id, 2);
    });

    it("prefers the earlier entry when two are equidistant", () => {
        const entries = [
            entry({ id: 1, datetime: localDate(2025, 5, 1).toISOString() }),
            entry({ id: 2, datetime: localDate(2025, 6, 30).toISOString() })
        ];

        const nearest = ForecastUtils.findNearestEntry("2025-06-15", entries);

        assert.equal(nearest.id, 1);
    });
});

describe("ForecastUtils.getEntryValue", () => {
    it("returns the real value of the position (obtained), not the invested amount", () => {
        assert.equal(ForecastUtils.getEntryValue(entry({ totalInvestedAmount: 1000, obtained: 1200 })), 1200);
    });

    it("keeps a zero obtained instead of falling back", () => {
        assert.equal(ForecastUtils.getEntryValue(entry({ totalInvestedAmount: 1000, obtained: 0 })), 0);
    });

    it("falls back to the invested amount when obtained is missing", () => {
        const withoutObtained = entry();
        delete withoutObtained.obtained;

        assert.equal(ForecastUtils.getEntryValue(withoutObtained), 1000);
    });

    it("returns 0 when there is no entry", () => {
        assert.equal(ForecastUtils.getEntryValue(null), 0);
    });
});

describe("ForecastUtils.toDailyRate", () => {
    it("returns 0 for a 0% monthly rate", () => {
        assert.equal(ForecastUtils.toDailyRate(0), 0);
    });

    it("converts a monthly percentage into its daily compounded equivalent", () => {
        const dailyRate = ForecastUtils.toDailyRate(0.471);

        assert.equal(dailyRate, Math.pow(1.00471, 1 / 30) - 1);
    });

    it("caps a total loss rate at -1 instead of returning NaN", () => {
        assert.equal(ForecastUtils.toDailyRate(-100), -1);
        assert.equal(ForecastUtils.toDailyRate(-150), -1);
    });
});

describe("ForecastUtils.generateScenarioData", () => {
    it("starts from the real value of the position, so it is comparable with the Real line", () => {
        const data = ForecastUtils.generateScenarioData(
            forecast({ startDate: "2026-01-05", endDate: "2026-01-05", scenarioRates: { PESSIMIST: 0, NEUTRAL: 0, OPTIMIST: 0 } }),
            "NEUTRAL",
            [entry()]
        );

        assert.equal(data[0].y, 1200);
    });

    it("emits the start point plus one point per day in ascending order", () => {
        const data = ForecastUtils.generateScenarioData(
            forecast({ startDate: "2026-01-01", endDate: "2026-01-31" }),
            "NEUTRAL",
            [entry()]
        );

        // 1 start point + 30 days
        assert.equal(data.length, 31);
        assert.equal(data[0].x, new Date("2026-01-01").getTime());
        assert.equal(data[data.length - 1].x, new Date("2026-01-31").getTime());

        const timestamps = data.map((point) => point.x);
        assert.deepEqual([...timestamps].sort((a, b) => a - b), timestamps);
        assert.ok(data.every((point, index) => index === 0 || point.y >= data[index - 1].y));
    });

    it("anchors on the nearest entry with a visual bridge only when it is not in the future", () => {
        const entryBefore = localDate(2025, 11, 30).toISOString();
        const entryAfter = localDate(2026, 0, 20).toISOString();
        const startDate = new Date("2026-01-01").getTime();

        const before = ForecastUtils.generateScenarioData(
            forecast({ startDate: "2026-01-01", endDate: "2026-01-31" }),
            "NEUTRAL",
            [entry({ datetime: entryBefore })]
        );
        assert.equal(before[0].x, new Date(entryBefore).getTime());
        assert.equal(before[0].y, 1200);

        const after = ForecastUtils.generateScenarioData(
            forecast({ startDate: "2026-01-01", endDate: "2026-01-31" }),
            "NEUTRAL",
            [entry({ datetime: entryAfter })]
        );
        assert.equal(after[0].x, startDate);
    });

    it("keeps a flat line when the rate and the contribution are zero", () => {
        const data = ForecastUtils.generateScenarioData(
            forecast({ scenarioRates: { PESSIMIST: 0, NEUTRAL: 0, OPTIMIST: 0 }, monthlyContribution: 0 }),
            "NEUTRAL",
            [entry()]
        );

        assert.ok(data.every((point) => point.y === 1200));
    });

    it("adds the monthly contribution prorated over the days of the forecast", () => {
        const data = ForecastUtils.generateScenarioData(
            forecast({ scenarioRates: { PESSIMIST: 0, NEUTRAL: 0, OPTIMIST: 0 }, monthlyContribution: 300 }),
            "NEUTRAL",
            [entry()]
        );

        // 30 days at 300 / 30 per day
        assert.equal(data[data.length - 1].y, 1500);
    });

    it("compounds the monthly growth rate without any contribution", () => {
        const data = ForecastUtils.generateScenarioData(
            forecast({ scenarioRates: { PESSIMIST: 0.5, NEUTRAL: 0.5, OPTIMIST: 0.5 }, monthlyContribution: 0 }),
            "NEUTRAL",
            [entry()]
        );

        assert.ok(Math.abs(data[data.length - 1].y - 1200 * 1.005) < 0.01);
    });

    it("compounds growth and contributions together over 30 days", () => {
        const data = ForecastUtils.generateScenarioData(
            forecast({ scenarioRates: { PESSIMIST: 0.5, NEUTRAL: 0.5, OPTIMIST: 0.5 }, monthlyContribution: 300 }),
            "NEUTRAL",
            [entry()]
        );

        const dailyRate = Math.pow(1.005, 1 / 30) - 1;
        const growth = Math.pow(1 + dailyRate, 30);
        const expected = 1200 * growth + (300 / 30) * ((growth - 1) / dailyRate);

        assert.ok(Math.abs(data[data.length - 1].y - expected) < 0.01);
    });

    it("keeps the ordering optimistic above neutral above pessimist", () => {
        const f = forecast();
        const entries = [entry()];
        const finalValue = (scenario) => {
            const data = ForecastUtils.generateScenarioData(f, scenario, entries);
            return data[data.length - 1].y;
        };

        assert.ok(finalValue("PESSIMIST") < finalValue("NEUTRAL"));
        assert.ok(finalValue("NEUTRAL") < finalValue("OPTIMIST"));
    });

    it("treats a missing monthly contribution as zero", () => {
        const f = forecast({ scenarioRates: { PESSIMIST: 0, NEUTRAL: 0, OPTIMIST: 0 } });
        delete f.monthlyContribution;

        const data = ForecastUtils.generateScenarioData(f, "NEUTRAL", [entry()]);

        assert.ok(data.every((point) => point.y === 1200));
    });

    it("treats a missing scenario rate as zero", () => {
        const data = ForecastUtils.generateScenarioData(forecast({ scenarioRates: {} }), "NEUTRAL", [entry()]);

        assert.ok(data.every((point) => point.y === 1200));
    });

    it("never returns NaN on a total loss scenario", () => {
        const data = ForecastUtils.generateScenarioData(
            forecast({ scenarioRates: { PESSIMIST: -100, NEUTRAL: 0, OPTIMIST: 0 }, monthlyContribution: 300 }),
            "PESSIMIST",
            [entry()]
        );

        assert.ok(data.every((point) => Number.isFinite(point.y)));
        assert.ok(data.every((point) => point.y >= 0));
        // A total loss wipes the position, so only the prorated contribution is left
        assert.equal(data[data.length - 1].y, 300 / 30);
    });

    it("projects from zero when there are no entries at all", () => {
        const data = ForecastUtils.generateScenarioData(
            forecast({ scenarioRates: { PESSIMIST: 0, NEUTRAL: 0, OPTIMIST: 0 }, monthlyContribution: 100 }),
            "NEUTRAL",
            []
        );

        assert.equal(data[0].y, 0);
        assert.equal(data[data.length - 1].y, 100);
    });
});

describe("ForecastUtils.interpolateRealData", () => {
    it("returns the single entry untouched when there is nothing to interpolate", () => {
        const data = ForecastUtils.interpolateRealData([entry({ totalInvestedAmount: 1200 })]);

        assert.equal(data.length, 1);
        assert.equal(data[0].y, 1200);
    });

    it("interpolates linearly between two entries", () => {
        const entries = [
            entry({ id: 1, datetime: localDate(2025, 0, 1).toISOString(), totalInvestedAmount: 1000 }),
            entry({ id: 2, datetime: localDate(2025, 0, 11).toISOString(), totalInvestedAmount: 2000 })
        ];

        const data = ForecastUtils.interpolateRealData(entries);

        assert.equal(data.length, 11);
        assert.equal(data[0].y, 1000);
        assert.equal(data[data.length - 1].y, 2000);
    });
});