import { useState, useMemo } from "react";
import Chart from "react-apexcharts";
import type { ApexOptions } from "apexcharts";
import { currencyAdapter, numberAdapter } from "@/lib/currencyAdapter";
import BaseModal from "./BaseModal";
import ModalHeader from "./ModalHeader";
import InvestmentStats from "../investments/InvestmentStats";
import ViewDetailsButton from "../investments/ViewDetailsButton";
import { getCustomTooltip } from "@/lib/ChartUtils";

interface InvestmentEntry {
    datetime: string;
    initialInvestedAmount: number;
    totalInvestedAmount: number;
    obtained: number;
    benefit: number;
    profitability: number;
}

interface Investment {
    id: string;
    name: string;
    description: string;
    currency: string;
    entries: InvestmentEntry[];
    lastEntry: InvestmentEntry | null;
}

interface InvestmentGraphModalProps {
    investment: Investment | null;
    onClose: () => void;
    showDetailsButton?: boolean;
}

export default function InvestmentGraphModal({ investment, onClose, showDetailsButton }: InvestmentGraphModalProps) {
    const [expandedGraphs, setExpandedGraphs] = useState({
        invested: true,
        benefit: true,
        profitability: true
    });

    if (!investment) return null;

    const { currency } = investment;
    
    const currencyTooltip = useMemo(() => getCustomTooltip((val: number) => currencyAdapter(val, currency)), [currency]);
    const percentTooltip = useMemo(() => getCustomTooltip((val: number) => numberAdapter(val) + "%"), []);

    const sharedOptions: ApexOptions = {
        legend: {
            show: true,
            position: "top",
            horizontalAlign: "left",
            fontSize: "12px",
            fontFamily: "Outfit, sans-serif",
        },
        chart: {
            fontFamily: "Outfit, sans-serif",
            height: 250,
            type: "line",
            toolbar: { 
                show: false,
                tools: { download: false }
            },
            zoom: { enabled: false }
        },
        stroke: {
            curve: "straight",
            width: 2,
        },
        fill: {
            type: "gradient",
            gradient: {
                opacityFrom: 0.55,
                opacityTo: 0,
            },
        },
        markers: {
            size: 0,
            strokeColors: "#fff",
            strokeWidth: 2,
            hover: { size: 4 },
        },
        grid: {
            xaxis: { lines: { show: false } },
            yaxis: { lines: { show: true } },
        },
        dataLabels: { enabled: false },
        tooltip: {
            enabled: true,
            x: { format: "dd MMM yyyy" },
            custom: currencyTooltip as any
        },
        xaxis: {
            type: "datetime",
            labels: {
                style: {
                    fontSize: '10px',
                    fontFamily: 'Outfit, sans-serif'
                },
                formatter: (val: string) => new Date(val).toLocaleDateString()
            }
        },
        yaxis: {
            labels: {
                style: { 
                    fontSize: "10px", 
                    colors: ["#6B7280"],
                    fontFamily: 'Outfit, sans-serif'
                },
                formatter: (value: number) => currencyAdapter(value, currency),
            },
        },
    };

    const seriesInvestedObtained = [
        {
            name: "Total Invested",
            data: investment.entries.map(e => ({ x: new Date(e.datetime).getTime(), y: e.totalInvestedAmount })),
        },
        {
            name: "Obtained",
            data: investment.entries.map(e => ({ x: new Date(e.datetime).getTime(), y: e.obtained })),
        },
    ];

    const seriesBenefit = [
        {
            name: "Benefit",
            data: investment.entries.map(e => ({ x: new Date(e.datetime).getTime(), y: e.benefit })),
        },
    ];

    const seriesProfitability = [
        {
            name: "Profitability",
            data: investment.entries.map(entry => ({ x: new Date(entry.datetime).getTime(), y: entry.profitability * 100 })),
        },
    ];

    return (
        <BaseModal onClose={onClose}>
            <ModalHeader 
                title={investment.name}
                description={investment.description}
                currency={currency}
                onClose={onClose}
            />
            
            <div className="p-4">
                <InvestmentStats investment={investment} currency={currency} />
                
                {showDetailsButton && (
                    <ViewDetailsButton investmentId={investment.id} className="mt-4" />
                )}

                <div className="mt-8 space-y-6">
                    {/* Invested vs Obtained */}
                    <div className="bg-gray-50 rounded-lg p-3 sm:p-4">
                        <h3 
                            className="text-sm sm:text-base font-semibold text-gray-700 mb-3 cursor-pointer flex justify-between items-center"
                            onClick={() => setExpandedGraphs(prev => ({...prev, invested: !prev.invested}))}
                        >
                            <span>Invested vs Obtained</span>
                            <span>{expandedGraphs.invested ? '▲' : '▼'}</span>
                        </h3>
                        {expandedGraphs.invested && (
                            <Chart
                                options={{
                                    ...sharedOptions,
                                    colors: ["#465FFF", "#9CB9FF"],
                                }}
                                series={seriesInvestedObtained}
                                type="area"
                                height={250}
                            />
                        )}
                    </div>

                    {/* Benefit */}
                    <div className="bg-gray-50 rounded-lg p-3 sm:p-4">
                        <h3 
                            className="text-sm sm:text-base font-semibold text-gray-700 mb-3 cursor-pointer flex justify-between items-center"
                            onClick={() => setExpandedGraphs(prev => ({...prev, benefit: !prev.benefit}))}
                        >
                            <span>Benefit Over Time</span>
                            <span>{expandedGraphs.benefit ? '▲' : '▼'}</span>
                        </h3>
                        {expandedGraphs.benefit && (
                            <Chart
                                options={{
                                    ...sharedOptions,
                                    colors: ["#10B981"],
                                }}
                                series={seriesBenefit}
                                type="area"
                                height={250}
                            />
                        )}
                    </div>

                    {/* Profitability */}
                    <div className="bg-gray-50 rounded-lg p-3 sm:p-4">
                        <h3 
                            className="text-sm sm:text-base font-semibold text-gray-700 mb-3 cursor-pointer flex justify-between items-center"
                            onClick={() => setExpandedGraphs(prev => ({...prev, profitability: !prev.profitability}))}
                        >
                            <span>Profitability Over Time</span>
                            <span>{expandedGraphs.profitability ? '▲' : '▼'}</span>
                        </h3>
                        {expandedGraphs.profitability && (
                            <Chart
                                options={{
                                    ...sharedOptions,
                                    colors: ["#F59E0B"],
                                    yaxis: {
                                        ...sharedOptions.yaxis,
                                        labels: {
                                            ...sharedOptions.yaxis?.labels,
                                            formatter: (value: number) => numberAdapter(value, 1) + "%",
                                        },
                                    },
                                    tooltip: {
                                        ...sharedOptions.tooltip,
                                        custom: percentTooltip as any
                                    },
                                }}
                                series={seriesProfitability}
                                type="area"
                                height={250}
                            />
                        )}
                    </div>
                </div>
            </div>
        </BaseModal>
    );
}
