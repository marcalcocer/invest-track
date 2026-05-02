import { useMemo, useState } from "react";
import Chart from "react-apexcharts";
import ForecastGraphModal from "./ForecastGraphModal";
import { currencyAdapter } from "@/lib/currencyAdapter";
import { ForecastUtils } from "@/lib/ForecastUtils";

export default function CombinedForecastGraphModal({ investment, forecasts, onClose }) {
    // Prepare real data
    const realData = useMemo(() => ForecastUtils.interpolateRealData(investment.entries.map(e => ({...e, totalInvestedAmount: e.obtained}))), [investment]);

    // Build all scenario lines from all forecasts using shared utility
    const allScenarioSeries = forecasts.flatMap((forecast) =>
        ["PESSIMIST", "NEUTRAL", "OPTIMIST"].map(scenario => {
            const data = ForecastUtils.generateScenarioData(forecast, scenario, investment.entries);
            return {
                name: `${forecast.name} - ${scenario.charAt(0) + scenario.slice(1).toLowerCase()}`,
                data: data
            };
        })
    );

    const [selectedForecast, setSelectedForecast] = useState(null);
    return (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50 overflow-y-auto">
            <div className="bg-white rounded-lg shadow-lg p-6 max-w-3xl w-full relative">
                <button className="absolute top-2 right-2 text-gray-500 hover:text-gray-700" onClick={onClose}>&times;</button>
                <h2 className="text-lg font-bold mb-4">Combined Forecasts Graph</h2>
                <div className="mb-6 flex justify-center">
                    <button
                        className="px-4 py-2 bg-blue-500 text-white text-sm font-semibold rounded-lg shadow hover:bg-blue-600 transition-colors"
                        onClick={() => window.location.href = `/investment?id=${investment.id}`}
                    >
                        View Details
                    </button>
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
