import { useMemo, useState } from "react";
import Chart from "react-apexcharts";
import ForecastGraphModal from "./ForecastGraphModal";
import { currencyAdapter } from "@/lib/currencyAdapter";
import { formatDate } from "@/lib/datetimeFormater";

export default function CombinedForecastGraphModal({ investment, forecasts, onClose }) {
    // Prepare real data
    const realData = useMemo(() => investment.entries.map(e => ({
        date: formatDate(e.datetime),
        value: e.totalInvestedAmount
    })), [investment]);
    // Calculate forecast months from startDate/endDate
    function getForecastMonths(f) {
        if (f.startDate && f.endDate) {
            const start = new Date(f.startDate);
            const end = new Date(f.endDate);
            return (end.getFullYear() - start.getFullYear()) * 12 + (end.getMonth() - start.getMonth()) + 1;
        }
        return realData.length;
    }
    const maxForecastMonths = Math.max(realData.length, ...forecasts.map(getForecastMonths));
    // Build all scenario lines from all forecasts
    const allScenarioSeries = forecasts.flatMap((forecast, idx) =>
        ["PESSIMIST", "NEUTRAL", "OPTIMIST"].map(scenario => {
            let values = [];
            let last = realData[0]?.value || 0;
            for (let i = 0; i < maxForecastMonths; i++) {
                values.push(Number(last.toFixed(2)));
                last = last * (1 + (forecast.scenarioRates?.[scenario] ?? 0) / 100);
            }
            return {
                name: `${forecast.name} - ${scenario.charAt(0) + scenario.slice(1).toLowerCase()}`,
                data: values
            };
        })
    );
    const categories = [
    ...realData.map(e => e.date),
    ...Array.from({ length: maxForecastMonths - realData.length }, (_, i) => `Mes ${realData.length + i + 1}`)
    ];
    const [selectedForecast, setSelectedForecast] = useState(null);
    return (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50 overflow-y-auto">
            <div className="bg-white rounded-lg shadow-lg p-6 max-w-3xl w-full relative">
                <button className="absolute top-2 right-2 text-gray-500 hover:text-gray-700" onClick={onClose}>&times;</button>
                <h2 className="text-lg font-bold mb-4">Combined Forecasts Graph</h2>
                <Chart
                    options={{
                        chart: { type: "line", height: 350 },
                        xaxis: { categories },
                        yaxis: {
                            labels: {
                                formatter: (value) => currencyAdapter(value, investment?.currency)
                            }
                        },
                        stroke: { width: 2 },
                        legend: { show: true },
                        tooltip: {
                            enabled: true,
                            custom: function({ series, seriesIndex, dataPointIndex, w }) {
                                // Collect all series values at the hovered point
                                const values = w.config.series.map((s, idx) => ({
                            name: s.name,
                            value: s.data[dataPointIndex],
                            color: w.globals.colors && w.globals.colors[idx] ? w.globals.colors[idx] : (w.config.colors ? w.config.colors[idx] : undefined), originalIndex: idx
                        }));
                        // Order by value descending
                        values.sort((a, b) => b.value - a.value);
                                // Build tooltip HTML
                                return `<div style='min-width:180px'>` + values.map(v =>
                                    `<div style='display:flex;align-items:center;margin-bottom:2px;'>` +
                                    (v.color ? `<span style='display:inline-block;width:10px;height:10px;background:${v.color};margin-right:6px;border-radius:50%'></span>` : "") +
                                    `<span style='font-weight:500'>${v.name}</span>: <span style='margin-left:4px'>${currencyAdapter(v.value, investment?.currency)}</span>` +
                                    `</div>`
                                ).join("") + `</div>`;
                            }
                        },
                    }}
                    series={[
                        {
                            name: "Real",
                            data: [
                                ...realData.map(e => Number(e.value.toFixed(2))),
                                ...Array(maxForecastMonths - realData.length).fill(null)
                            ]
                        },
                        ...allScenarioSeries
                    ]}
                    type="line"
                    height={350}
                />
                <div className="mt-8 space-y-4">
                    <h3 className="font-semibold mb-2">Ver detalles de forecast:</h3>
                    <ul className="space-y-2">
                        {forecasts.map(forecast => (
                            <li key={forecast.id || forecast.name}>
                                <button
                                    className="px-3 py-1 bg-blue-100 hover:bg-blue-200 rounded text-blue-800 text-sm"
                                    onClick={() => setSelectedForecast(forecast)}
                                >
                                    {forecast.name}
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
                {selectedForecast && (
                    <ForecastGraphModal
                        forecast={selectedForecast}
                        entries={investment.entries}
                        investment={investment}
                        onClose={() => setSelectedForecast(null)}
                    />
                )}
            </div>
        </div>
    );
}
