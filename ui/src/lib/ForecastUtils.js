/**
 * Utility functions for Forecast calculations and graphing.
 */
export const ForecastUtils = {
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
     * Generates a series of data points for a forecast scenario,
     * including the visual bridge from the nearest entry.
     * Calculations are performed daily for high granularity.
     */
    generateScenarioData(forecast, scenario, entries) {
        const nearestEntry = this.findNearestEntry(forecast.startDate, entries);
        const baselineValue = nearestEntry ? nearestEntry.totalInvestedAmount : 0;
        
        let data = [];
        if (nearestEntry) {
            // Visual Bridge: start from nearest entry
            data.push({ x: new Date(nearestEntry.datetime).getTime(), y: Number(baselineValue.toFixed(2)) });
        }
        
        let lastValue = baselineValue;
        const start = new Date(forecast.startDate);
        const end = new Date(forecast.endDate);
        
        // Calculate daily rate from monthly rate: (1 + monthlyRate)^(1/30) - 1
        // monthlyRate is provided as a percentage (e.g., 2.5)
        const monthlyRateDecimal = (forecast.scenarioRates?.[scenario] ?? 0) / 100;
        const dailyRate = Math.pow(1 + monthlyRateDecimal, 1 / 30) - 1;

        // Start of forecast
        data.push({ x: start.getTime(), y: Number(lastValue.toFixed(2)) });

        let current = new Date(start);
        // Increment daily
        while (current < end) {
            current.setDate(current.getDate() + 1);
            if (current > end) break;
            
            lastValue = lastValue * (1 + dailyRate);
            data.push({ x: current.getTime(), y: Number(lastValue.toFixed(2)) });
        }
        return data;
    }
};
