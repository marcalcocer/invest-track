import { useState, useMemo } from "react";
import Chart from "react-apexcharts";
import { formatValueForPrivacy } from "@/lib/currencyAdapter";
import { ForecastUtils } from "@/lib/ForecastUtils";
import BaseModal from "./BaseModal";
import ModalHeader from "./ModalHeader";
import { getCustomTooltip, getTodayAnnotation } from "@/lib/ChartUtils";
import { withChartDefaults } from "@/lib/chartTheme";
import { useIsDarkMode } from "@/lib/useIsDarkMode";

export default function ForecastGraphModal({ forecast, entries, onClose, investment, isPrivate }) {
    const [visibleScenarios, setVisibleScenarios] = useState({ PESSIMIST: true, NEUTRAL: true, OPTIMIST: true });

    const customTooltip = useMemo(
        () => getCustomTooltip((val) => formatValueForPrivacy(val, investment?.currency, isPrivate)),
        [investment?.currency, isPrivate]
    );

    const isDark = useIsDarkMode();

    if (!forecast) return null;

    // Prepare real data (entries) with interpolation
    const realData = ForecastUtils.interpolateRealData(entries.map(e => ({...e, totalInvestedAmount: e.obtained})));

    // Prepare forecast data for each scenario using shared utility
    const scenarioSeries = ["PESSIMIST", "NEUTRAL", "OPTIMIST"].map(scenario => {
        const data = ForecastUtils.generateScenarioData(forecast, scenario, entries);
        return { name: scenario.charAt(0) + scenario.slice(1).toLowerCase(), key: scenario, data };
    });

    return (
        <BaseModal onClose={onClose} maxWidth="max-w-4xl">
            <ModalHeader 
                title={`Forecast Graph: ${forecast.name}`} 
                description={investment?.name}
                currency={investment?.currency}
                onClose={onClose}
            />
            <div className="p-6">
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
                        ...scenarioSeries.filter(s => visibleScenarios[s.key])
                    ]}
                    type="line"
                    height={350}
                />
            </div>
        </BaseModal>
    );
}
