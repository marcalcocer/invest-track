/**
 * Utility functions for Forecast calculations and graphing.
 */

// The projection compounds on a fixed 30 day "month", so a monthly growth rate
// and a monthly contribution are both prorated over this amount of days.
const DAYS_PER_MONTH = 30;

export const ForecastUtils = {
    /**
     * Adds calendar months to an ISO date (YYYY-MM-DD) and returns a new ISO date.
     * The arithmetic is done on the date parts, so it does not depend on the
     * timezone, and it is clamped to the end of the month: adding a month to
     * the 31st lands on the 28th or 29th instead of overflowing into the next one.
     */
    addMonths(isoDate, months) {
        var parts = String(isoDate).split("-").map(Number);
        if (parts.length !== 3 || !parts.every(Number.isFinite)) return null;

        const [year, month, day] = parts;
        var shifted = new Date(Date.UTC(year, month - 1 + Number(months), 1));
        var lastDayOfMonth = new Date(Date.UTC(shifted.getUTCFullYear(), shifted.getUTCMonth() + 1, 0)).getUTCDate();

        return new Date(Date.UTC(shifted.getUTCFullYear(), shifted.getUTCMonth(), Math.min(day, lastDayOfMonth)))
            .toISOString()
            .slice(0, 10);
    },

    /**
     * Whole months between two ISO dates, so monthsBetween(addMonths(start, n)) === n.
     * A forecast always lasts at least one month.
     */
    monthsBetween(startIsoDate, endIsoDate) {
        var start = String(startIsoDate).split("-").map(Number);
        var end = String(endIsoDate).split("-").map(Number);
        if (start.length !== 3 || end.length !== 3) return 1;
        if (!start.every(Number.isFinite) || !end.every(Number.isFinite)) return 1;

        const months = (end[0] - start[0]) * 12 + (end[1] - start[1]);
        return Math.max(1, months);
    },

    /**
     * Finds the entry closest to the target date.
     * If equidistant, the earlier entry is preferred.
     */
    findNearestEntry(targetDate, entriesList) {
        if (!entriesList || entriesList.length === 0) return null;
        const target = new Date(targetDate).getTime();
        let nearest = entriesList[0];
        let minDiff = Math.abs(new Date(entriesList[0].datetime).getTime() - target);

        for (let i = 1; i < entriesList.length; i++) {
            const currentDiff = Math.abs(new Date(entriesList[i].datetime).getTime() - target);
            if (currentDiff < minDiff) {
                minDiff = currentDiff;
                nearest = entriesList[i];
            } else if (currentDiff === minDiff) {
                // If equidistant, prefer the earlier one
                if (new Date(entriesList[i].datetime).getTime() < new Date(nearest.datetime).getTime()) {
                    nearest = entriesList[i];
                }
            }
        }
        return nearest;
    },

    /**
     * Interpolates real data entries to provide daily data points.
     * Uses linear interpolation between manual entries.
     */
    interpolateRealData(entries) {
        if (!entries || entries.length < 2) {
            return entries ? entries.map(e => ({ x: new Date(e.datetime).getTime(), y: Number(e.totalInvestedAmount.toFixed(2)) })) : [];
        }

        const sorted = [...entries].sort((a, b) => new Date(a.datetime).getTime() - new Date(b.datetime).getTime());
        let interpolated = [];

        for (let i = 0; i < sorted.length - 1; i++) {
            const e1 = sorted[i];
            const e2 = sorted[i + 1];
            const start = new Date(e1.datetime);
            const end = new Date(e2.datetime);
            const val1 = e1.totalInvestedAmount;
            const val2 = e2.totalInvestedAmount;

            const diffDays = Math.max(1, (end - start) / (1000 * 60 * 60 * 24));
            const slope = (val2 - val1) / diffDays;

            for (let d = 0; d < diffDays; d++) {
                const currentDate = new Date(start);
                currentDate.setDate(currentDate.getDate() + d);
                const value = val1 + slope * d;
                interpolated.push({ x: currentDate.getTime(), y: Number(value.toFixed(2)) });
            }
        }
        // Add the last point
        const lastEntry = sorted[sorted.length - 1];
        interpolated.push({ x: new Date(lastEntry.datetime).getTime(), y: Number(lastEntry.totalInvestedAmount.toFixed(2)) });

        return interpolated;
    },

    /**
     * Real value of an entry: the current value of the position (obtained),
     * which already includes the accumulated benefit. Falls back to the
     * invested amount when obtained is not available.
     */
    getEntryValue(entry) {
        if (!entry) return 0;
        return entry.obtained ?? entry.totalInvestedAmount ?? 0;
    },

    /**
     * Converts a monthly growth percentage (e.g. 0.471 for 0.471% per month)
     * into its daily compounded equivalent.
     */
    toDailyRate(monthlyRatePercentage) {
        const monthlyRate = Number(monthlyRatePercentage) / 100;
        // A rate of -100% or worse cannot be compounded any further
        if (monthlyRate <= -1) return -1;
        return Math.pow(1 + monthlyRate, 1 / DAYS_PER_MONTH) - 1;
    },

    /**
     * Generates a series of data points for a forecast scenario,
     * including the visual bridge from the nearest entry.
     * The projection starts from the real value of the position (obtained) and
     * applies the monthly growth rate plus the monthly contribution.
     * Calculations are performed daily for high granularity.
     */
    generateScenarioData(forecast, scenario, entries) {
        const start = new Date(forecast.startDate);
        const end = new Date(forecast.endDate);
        const nearestEntry = this.findNearestEntry(forecast.startDate, entries);
        const baselineValue = this.getEntryValue(nearestEntry);

        let data = [];
        if (nearestEntry && new Date(nearestEntry.datetime) <= start) {
            // Visual Bridge: anchor on the real value of the nearest entry
            data.push({ x: new Date(nearestEntry.datetime).getTime(), y: Number(baselineValue.toFixed(2)) });
        }

        let lastValue = baselineValue;

        // Start of forecast
        data.push({ x: start.getTime(), y: Number(lastValue.toFixed(2)) });

        // scenarioRates are monthly percentages (e.g. 0.471 means 0.471% per month)
        const dailyRate = this.toDailyRate(forecast.scenarioRates?.[scenario] ?? 0);
        const dailyContribution = (forecast.monthlyContribution ?? 0) / DAYS_PER_MONTH;

        let current = new Date(start);
        // Increment daily
        while (current < end) {
            current.setDate(current.getDate() + 1);
            if (current > end) break;

            lastValue = lastValue * (1 + dailyRate) + dailyContribution;
            data.push({ x: current.getTime(), y: Number(lastValue.toFixed(2)) });
        }
        return data;
    }
};
