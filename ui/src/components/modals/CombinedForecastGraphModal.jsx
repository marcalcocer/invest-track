import { useMemo, useState } from "react";
import Chart from "react-apexcharts";
import ForecastGraphModal from "./ForecastGraphModal";
import { formatValueForPrivacy } from "@/lib/currencyAdapter";
import { ForecastUtils } from "@/lib/ForecastUtils";
import BaseModal from "./BaseModal";
import ModalHeader from "./ModalHeader";
import ViewDetailsButton from "../investments/ViewDetailsButton";
import { getCustomTooltip, getTodayAnnotation } from "@/lib/ChartUtils";
import { withChartDefaults } from "@/lib/chartTheme";
import { useIsDarkMode } from "@/lib/useIsDarkMode";

export default function CombinedForecastGraphModal({ investment, forecasts, onClose, isPrivate }) {
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
    const isDark = useIsDarkMode();
    const customTooltip = useMemo(
        () => getCustomTooltip((val) => formatValueForPrivacy(val, investment?.currency, isPrivate)),
        [investment?.currency, isPrivate]
    );

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
                    options={withChartDefaults({
                        chart: {
                            type: "line",
                            height: 350
                        },
                        xaxis: { 
                            type: 'datetime',
                            labels: {
                                formatter: (val) => new Date(val).toLocaleDateString()
                            }
                        },
                        yaxis: {
                            labels: {
                                formatter: (value) => formatValueForPrivacy(value, investment?.currency, isPrivate)
                            }
                        },
                        stroke: { width: 2 },
                        legend: { show: true },
                        annotations: getTodayAnnotation(),
                        tooltip: {
                            enabled: true,
                            x: { format: 'dd MMM yyyy' },
                            custom: customTooltip
                        },
                    }, isDark)}
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
                                    className="px-3 py-1 bg-primary/15 hover:bg-primary/25 rounded text-primary text-sm"
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
                    isPrivate={isPrivate}
                    onClose={() => setSelectedForecast(null)}
                />
            )}
        </BaseModal>
    );
}
