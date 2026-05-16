import { useMemo, useState } from "react";
import Chart from "react-apexcharts";
import ForecastGraphModal from "./ForecastGraphModal";
import { currencyAdapter } from "@/lib/currencyAdapter";
import { ForecastUtils } from "@/lib/ForecastUtils";
import BaseModal from "./BaseModal";
import ModalHeader from "./ModalHeader";
import ViewDetailsButton from "../investments/ViewDetailsButton";
import { getCustomTooltip } from "@/lib/ChartUtils";

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
    const customTooltip = useMemo(() => getCustomTooltip((val) => currencyAdapter(val, investment?.currency)), [investment?.currency]);

    return (
        <BaseModal onClose={onClose} maxWidth="max-w-4xl">
            <ModalHeader 
                title="Combined Forecasts Graph" 
                description={investment.name}
                currency={investment.currency}
                onClose={onClose}
            />
            <div className="p-6">
                <ViewDetailsButton investmentId={investment.id} className="mb-6" />
                
                <Chart
                    options={{
                        chart: { 
                            type: "line", 
                            height: 350, 
                            zoom: { autoScaleYaxis: true },
                            toolbar: { show: false }
                        },
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
                            custom: customTooltip
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
            </div>
            {selectedForecast && (
                <ForecastGraphModal
                    forecast={selectedForecast}
                    entries={investment.entries}
                    investment={investment}
                    onClose={() => setSelectedForecast(null)}
                />
            )}
        </BaseModal>
    );
}
