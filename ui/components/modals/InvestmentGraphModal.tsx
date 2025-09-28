import { useState } from "react";
import Chart from "react-apexcharts";
import type { ApexOptions } from "apexcharts";
import { currencyAdapter } from "@/lib/currencyAdapter";
import { formatDate } from "@/lib/datetimeFormater";

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
    const categories = investment.entries.map(entry => formatDate(entry.datetime));

    const formatCurrencyLabel = (value: number) => {
        const formatted = currencyAdapter(value, currency);
        return formatted.replace(/[0-9.,\s]/g, "").trim();
    };

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
            style: {
                fontSize: '12px',
                fontFamily: 'Outfit, sans-serif'
            }
        },
        xaxis: {
            type: "category",
            categories,
            axisBorder: { show: false },
            axisTicks: { show: false },
            labels: {
                style: {
                    fontSize: '10px',
                    fontFamily: 'Outfit, sans-serif'
                }
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
            title: {
                text: formatCurrencyLabel(0),
                style: {
                    fontSize: "12px",
                    color: "#6B7280",
                    fontFamily: 'Outfit, sans-serif'
                },
            },
        },
    };

    const entryData = investment.entries.map(entry => ({
        date: formatDate(entry.datetime),
        totalInvestedAmount: entry.totalInvestedAmount,
        obtained: entry.obtained,
        benefit: entry.benefit,
    }));

    const seriesInvestedObtained = [
        {
            name: "Total Invested",
            data: entryData.map(e => e.totalInvestedAmount),
        },
        {
            name: "Obtained",
            data: entryData.map(e => e.obtained),
        },
    ];

    const seriesBenefit = [
        {
            name: "Benefit",
            data: entryData.map(e => e.benefit),
        },
    ];

    const seriesProfitability = [
        {
            name: "Profitability",
            data: investment.entries.map(entry => entry.profitability * 100),
        },
    ];

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-2 sm:p-4 z-50">
            <div className="bg-white rounded-lg shadow-lg w-full max-w-4xl max-h-[90vh] overflow-y-auto mx-2">
                {/* Header */}
                <div className="bg-white p-4 border-b border-gray-200 rounded-t-lg">
                    <div className="flex justify-between items-start">
                        <div className="flex-1 min-w-0">
                            <h2 className="text-lg sm:text-xl font-bold text-gray-800 truncate">{investment.name}</h2>
                            <p className="text-gray-600 text-xs sm:text-sm truncate">
                                {investment.description} ({investment.currency})
                            </p>
                        </div>
                        <button 
                            className="flex-shrink-0 ml-2 text-gray-500 hover:text-gray-800 text-lg"
                            onClick={onClose}
                        >
                            ✖
                        </button>
                    </div>
                    
                    {/* Stats in responsive grid */}
                    <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 mt-3">
                        <div className="text-center">
                            <p className="text-xs text-gray-500">Initial</p>
                            <p className="text-xs font-semibold text-gray-700">
                                {investment.lastEntry ? currencyAdapter(investment.lastEntry.initialInvestedAmount, currency) : "N/A"}
                            </p>
                        </div>
                        <div className="text-center">
                            <p className="text-xs text-gray-500">Total</p>
                            <p className="text-xs font-semibold text-gray-700">
                                {investment.lastEntry ? currencyAdapter(investment.lastEntry.totalInvestedAmount, currency) : "N/A"}
                            </p>
                        </div>
                        <div className="text-center">
                            <p className="text-xs text-gray-500">Obtained</p>
                            <p className="text-xs font-semibold text-gray-700">
                                {investment.lastEntry ? currencyAdapter(investment.lastEntry.obtained, currency) : "N/A"}
                            </p>
                        </div>
                        <div className="text-center">
                            <p className="text-xs text-gray-500">Profit</p>
                            <p className="text-xs font-semibold text-gray-700">
                                {investment.lastEntry ? (100 * investment.lastEntry.profitability).toFixed(2) + "%" : "N/A"}
                            </p>
                        </div>
                    </div>

                    {/* View Details Button */}
                    {showDetailsButton && (
                        <div className="mt-4 flex justify-center">
                            <button
                                className="px-4 py-2 bg-blue-500 text-white text-sm font-semibold rounded-lg shadow hover:bg-blue-600 transition-colors w-full sm:w-auto"
                                onClick={() => window.location.href = `/investment?id=${investment.id}`}
                            >
                                View Details
                            </button>
                        </div>
                    )}
                </div>

                {/* Scrollable content */}
                <div className="p-4">
                    {entryData.length > 0 ? (
                        <div className="space-y-6">
                            {/* Invested vs Obtained */}
                            <div className="bg-gray-50 rounded-lg p-3 sm:p-4">
                                <h3 
                                    className="text-sm sm:text-base font-semibold text-gray-700 mb-3 cursor-pointer"
                                    onClick={() => setExpandedGraphs(prev => ({...prev, invested: !prev.invested}))}
                                >
                                    Invested vs Obtained {expandedGraphs.invested ? '▲' : '▼'}
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
                                    className="text-sm sm:text-base font-semibold text-gray-700 mb-3 cursor-pointer"
                                    onClick={() => setExpandedGraphs(prev => ({...prev, benefit: !prev.benefit}))}
                                >
                                    Benefit Over Time {expandedGraphs.benefit ? '▲' : '▼'}
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
                                    className="text-sm sm:text-base font-semibold text-gray-700 mb-3 cursor-pointer"
                                    onClick={() => setExpandedGraphs(prev => ({...prev, profitability: !prev.profitability}))}
                                >
                                    Profitability Over Time {expandedGraphs.profitability ? '▲' : '▼'}
                                </h3>
                                {expandedGraphs.profitability && (
                                    <Chart
                                        options={{
                                            ...sharedOptions,
                                            colors: ["#F59E0B"],
                                            yaxis: {
                                                ...sharedOptions.yaxis,
                                                labels: {
                                                    style: { 
                                                        fontSize: "10px", 
                                                        colors: ["#6B7280"],
                                                        fontFamily: 'Outfit, sans-serif'
                                                    },
                                                    formatter: (value: number) => value.toFixed(1) + "%",
                                                },
                                                title: {
                                                    text: "%",
                                                    style: {
                                                        fontSize: "12px",
                                                        color: "#6B7280",
                                                        fontFamily: 'Outfit, sans-serif'
                                                    },
                                                },
                                            },
                                            tooltip: {
                                                ...sharedOptions.tooltip,
                                                y: {
                                                    formatter: (value: number) => value.toFixed(2) + "%",
                                                },
                                            },
                                        }}
                                        series={seriesProfitability}
                                        type="area"
                                        height={250}
                                    />
                                )}
                            </div>
                        </div>
                    ) : (
                        <p className="text-center text-gray-400 py-8">No investment entries available.</p>
                    )}
                </div>
            </div>
        </div>
    );
}