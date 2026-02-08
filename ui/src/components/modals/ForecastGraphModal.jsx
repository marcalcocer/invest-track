import { useState } from "react";
import Chart from "react-apexcharts";
import { formatDate } from "@/lib/datetimeFormater";
import { currencyAdapter } from "@/lib/currencyAdapter";

export default function ForecastGraphModal({ forecast, entries, onClose, investment }) {
    const [visibleScenarios, setVisibleScenarios] = useState({ PESSIMIST: true, NEUTRAL: true, OPTIMIST: true });
    if (!forecast) return null;

    // Prepare real data (entries)
    const realData = entries.map(e => ({
        date: formatDate(e.datetime),
        value: e.totalInvestedAmount
    }));
    // Prepare forecast data for each scenario
    let forecastMonths = realData.length;
    if (forecast.startDate && forecast.endDate) {
        const start = new Date(forecast.startDate);
        const end = new Date(forecast.endDate);
        forecastMonths = (end.getFullYear() - start.getFullYear()) * 12 + (end.getMonth() - start.getMonth()) + 1;
    }
    forecastMonths = Math.max(realData.length, forecastMonths);
    const scenarioSeries = ["PESSIMIST", "NEUTRAL", "OPTIMIST"].map(scenario => {
        let values = [];
        let last = realData[0]?.value || 0;
        for (let i = 0; i < forecastMonths; i++) {
            // Compound growth per month
            values.push(Number(last.toFixed(2)));
            last = last * (1 + (forecast.scenarioRates?.[scenario] ?? 0) / 100);
        }
        return { name: scenario.charAt(0) + scenario.slice(1).toLowerCase(), key: scenario, data: values };
    });
    const categories = [
    ...realData.map(e => e.date),
    ...Array.from({ length: forecastMonths - realData.length }, (_, i) => `Mes ${realData.length + i + 1}`)
    ];
    return (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg shadow-lg p-6 max-w-2xl w-full relative">
                <button className="absolute top-2 right-2 text-gray-500 hover:text-gray-700" onClick={onClose}>
                    &times;
                </button>
                <h2 className="text-lg font-bold mb-4">Forecast Graph: {forecast.name}</h2>
                <div className="mb-4 flex flex-col gap-2">
                    <div className="flex gap-3 items-center">
                        {['PESSIMIST', 'NEUTRAL', 'OPTIMIST'].map(scenario => (
                            <label key={scenario} className="flex items-center gap-1 text-xs cursor-pointer">
                                <input
                                    type="checkbox"
                                    checked={visibleScenarios[scenario]}
                                    onChange={() => setVisibleScenarios(v => ({ ...v, [scenario]: !v[scenario] }))}
                                />
                                {scenario.charAt(0) + scenario.slice(1).toLowerCase()}
                            </label>
                        ))}
                    </div>
                </div>
                <Chart
                    options={{
                        chart: { type: "line", height: 300 },
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
                                const values = w.config.series.map((s, idx) => ({
                                    name: s.name,
                                    value: s.data[dataPointIndex],
                                    color: w.globals.colors && w.globals.colors[idx] ? w.globals.colors[idx] : (w.config.colors ? w.config.colors[idx] : undefined)
                                }));
                                values.sort((a, b) => b.value - a.value);
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
                                ...Array(forecastMonths - realData.length).fill(null)
                            ]
                        },
                        ...scenarioSeries.filter(s => visibleScenarios[s.key])
                    ]}
                    type="line"
                    height={300}
                />
            </div>
        </div>
    );
}
