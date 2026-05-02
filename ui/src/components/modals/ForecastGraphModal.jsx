import { useState } from "react";
import Chart from "react-apexcharts";
import { currencyAdapter } from "@/lib/currencyAdapter";
import { ForecastUtils } from "@/lib/ForecastUtils";

export default function ForecastGraphModal({ forecast, entries, onClose, investment }) {
    const [visibleScenarios, setVisibleScenarios] = useState({ PESSIMIST: true, NEUTRAL: true, OPTIMIST: true });
    if (!forecast) return null;

    // Prepare real data (entries) with interpolation
    const realData = ForecastUtils.interpolateRealData(entries.map(e => ({...e, totalInvestedAmount: e.obtained})));

    // Prepare forecast data for each scenario using shared utility
    const scenarioSeries = ["PESSIMIST", "NEUTRAL", "OPTIMIST"].map(scenario => {
        const data = ForecastUtils.generateScenarioData(forecast, scenario, entries);
        return { name: scenario.charAt(0) + scenario.slice(1).toLowerCase(), key: scenario, data };
    });

    return (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg shadow-lg p-6 max-w-3xl w-full relative">
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
                        chart: { type: "line", height: 350, zoom: { autoScaleYaxis: true } },
                        xaxis: { 
                            type: 'datetime',
                            labels: {
                                formatter: (val) => new Date(val).toLocaleDateString()
                            }
                        },
                        yaxis: {
                            labels: {
                                formatter: (value) => currencyAdapter(value, investment?.currency)
                            }
                        },
                        stroke: { width: 2 },
                        legend: { show: true },
                        tooltip: {
                            enabled: true,
                            x: { format: 'dd MMM yyyy' },
                            custom: function({ series, seriesIndex, dataPointIndex, w }) {
                                const hoveredX = w.config.series[seriesIndex].data[dataPointIndex].x;
                                const values = w.config.series.map((s, idx) => {
                                    const dataPoint = s.data.find(d => d.x === hoveredX);
                                    return {
                                        name: s.name,
                                        value: dataPoint ? dataPoint.y : null,
                                        color: w.globals.colors && w.globals.colors[idx] ? w.globals.colors[idx] : (w.config.colors ? w.config.colors[idx] : undefined)
                                    };
                                }).filter(v => v.value !== null);
                                values.sort((a, b) => b.value - a.value);
                                return `<div style='min-width:180px; padding: 10px;'>` + values.map(v =>
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
                            data: realData
                        },
                        ...scenarioSeries.filter(s => visibleScenarios[s.key])
                    ]}
                    type="line"
                    height={350}
                />
            </div>
        </div>
    );
}
