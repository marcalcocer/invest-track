import { useEffect, useState } from "react";
import { InvestmentService } from "@/lib/InvestmentService";
import { currencyAdapter } from "@/lib/currencyAdapter";
import CreateEntryModal from "@/components/modals/CreateEntryModal";
import LoadingSpinner from "@/components/LoadingSpinner";
import { formatDatetime } from "@/lib/datetimeFormater";
import ConfirmDeleteModal from "@/components/modals/ConfirmDeleteModal";
import InvestmentGraphModal from "../modals/InvestmentGraphModal";
import CreateForecastModal from "../modals/CreateForecastModal";
import InvestmentForecastSection from "./InvestmentSection/InvestmentForecastSection";
import InvestmentEntriesMobile from "./InvestmentSection/InvestmentEntriesMobile";
import InvestmentEntriesTable from "./InvestmentSection/InvestmentEntriesTable";

export default function Investment() {
    const [investment, setInvestment] = useState(null);
    const [entries, setEntries] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isCreating, setIsCreating] = useState(false);
    const [isConfirmingDelete, setIsConfirmingDelete] = useState(null);
    const [isDeleting, setIsDeleting] = useState(null);
    const [showGraphModal, setShowGraphModal] = useState(false);
    const [showForecastModal, setShowForecastModal] = useState(false);
    // Forecasts state
    const [forecasts, setForecasts] = useState([]);
    const [isLoadingForecasts, setIsLoadingForecasts] = useState(true);
    const [isConfirmingDeleteForecast, setIsConfirmingDeleteForecast] = useState(null);

    // On mount, get the id from query params and fetch the investment details
    useEffect(() => {
        const searchParams = new URLSearchParams(window.location.search);
        const id = searchParams.get("id");

        if (!id) {
            setIsLoading(false);
            window.location.href = "/404";
            return;
        }

        InvestmentService.fetchInvestments().then((investmentsData) => {
            const inv = investmentsData.find((investment) => investment.id === Number(id));

            if (inv) {
                setInvestment(inv);
                setEntries(inv.entries);
                // Fetch forecasts for this investment
                setIsLoadingForecasts(true);
                InvestmentService.fetchForecasts(inv.id)
                    .then(setForecasts)
                    .catch(() => setForecasts([]))
                    .finally(() => setIsLoadingForecasts(false));
            } else {
                window.location.href = "/404";
            }

            setIsLoading(false);
        });
    }, []);

    const handleDeleteEntry = async (entryId) => {
        try {
            setIsDeleting(true);
            await InvestmentService.deleteInvestmentEntry(investment.id, entryId);
            window.location.reload();
        } catch (error) {
            console.error("Error deleting entry", error);
        } finally {
            setIsDeleting(false);
        }
    };

    const reload = () => {
        window.location.reload();
    };

    if (isLoading) return <LoadingSpinner />;

    const handleCreateForecast = async (forecastData) => {
        if (!investment) return;
        setShowForecastModal(false);
        setIsLoadingForecasts(true);
        try {
            await InvestmentService.createForecast(investment.id, forecastData);
            const updated = await InvestmentService.fetchForecasts(investment.id);
            setForecasts(updated);
        } catch (e) {
            // Optionally show error
        } finally {
            setIsLoadingForecasts(false);
        }
    };

    const handleDeleteForecast = async (forecastId) => {
        setIsConfirmingDeleteForecast(null);
        setIsLoadingForecasts(true);
        try {
            await InvestmentService.deleteForecast(forecastId);
            const updated = await InvestmentService.fetchForecasts(investment.id);
            setForecasts(updated);
        } catch (e) {
            // Optionally show error
        } finally {
            setIsLoadingForecasts(false);
        }
    };


    return (
        <div className="p-4 sm:p-6 max-w-3xl mx-auto">
            {isDeleting && <LoadingSpinner />}
            {showForecastModal && (
                <CreateForecastModal
                    investment={investment}
                    entries={entries}
                    onClose={() => setShowForecastModal(false)}
                    onCreate={handleCreateForecast}
                />
            )}

                <h2 className="text-lg sm:text-xl font-bold mb-3 sm:mb-4">{investment.name} - Entries</h2>
                <p className="text-gray-600 text-sm sm:text-base mb-4">
                    {investment.description} ({investment.currency})
                </p>
                {/* Mini-summary */}
                {entries.length > 0 && (
                <div className="flex flex-wrap gap-4 items-center justify-center mb-4 text-xs sm:text-sm text-gray-700 bg-gray-50 rounded-lg px-3 py-2 border border-gray-100">
                    <span>Entries: <span className="font-semibold">{entries.length}</span></span>
                    <span>Avg Profit: <span className="font-semibold">{(entries.reduce((acc, e) => acc + (e.profitability ?? 0), 0) / entries.length * 100).toFixed(2)}%</span></span>
                    <span>Last update: <span className="font-semibold">{formatDatetime(Math.max(...entries.map(e => new Date(e.datetime))))}</span></span>
                </div>
                )}

                <div className="flex flex-col sm:flex-row gap-2 sm:gap-0 justify-between mt-4 sm:mt-6">
                    <button
                        className="px-3 py-2 sm:px-4 sm:py-2 bg-green-500 text-white text-sm rounded shadow hover:bg-green-600 transition duration-200"
                        onClick={() => setIsCreating(true)}
                    >
                        Create Entry
                    </button>
                    <button
                        className="px-3 py-2 sm:px-4 sm:py-2 bg-blue-500 text-white text-sm rounded shadow hover:bg-blue-600 transition duration-200"
                        onClick={() => setShowGraphModal(true)}
                    >
                        Graph
                    </button>
                    <button
                        className="px-3 py-2 sm:px-4 sm:py-2 bg-purple-500 text-white text-sm rounded shadow hover:bg-purple-600 transition duration-200"
                        onClick={() => setShowForecastModal(true)}
                    >
                        Forecast
                    </button>
                    <button
                        className="px-3 py-2 sm:px-4 sm:py-2 bg-gray-500 text-white text-sm rounded shadow hover:bg-gray-600 transition duration-200"
                        onClick={() => window.history.back()}
                    >
                        Back
                    </button>
                </div>

                {/* Forecasts Section */}
                <InvestmentForecastSection
                    forecasts={forecasts}
                    isLoadingForecasts={isLoadingForecasts}
                    onDeleteForecast={handleDeleteForecast}
                    isConfirmingDeleteForecast={isConfirmingDeleteForecast}
                    setIsConfirmingDeleteForecast={setIsConfirmingDeleteForecast}
                />

                {/* Mobile Cards View */}
                <InvestmentEntriesMobile
                    entries={entries}
                    investment={investment}
                    setIsConfirmingDelete={setIsConfirmingDelete}
                />

                {/* Desktop Table View */}
                <div className="hidden sm:block overflow-x-auto mt-6">
                    <table className="w-full border-collapse border border-gray-200">
                        <thead>
                            <tr className="bg-gray-100">
                                <th className="border p-2 text-xs sm:text-sm">ID</th>
                                <th className="border p-2 text-xs sm:text-sm">Date</th>
                                <th className="border p-2 text-xs sm:text-sm">Total Invested</th>
                                <th className="border p-2 text-xs sm:text-sm">Profitability</th>
                                <th className="border p-2 text-xs sm:text-sm">Comments</th>
                                <th className="border p-2 text-xs sm:text-sm">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {entries.length > 0 ? (
                                entries.map((entry, idx) => (
                                    <tr
                                        key={entry.id}
                                        className={`border transition-colors ${idx % 2 === 1 ? 'bg-slate-50' : ''} hover:bg-slate-100`}
>
                                        <td className="border p-2 text-center text-xs sm:text-sm">{entry.id}</td>
                                        <td className="border p-2 text-center text-xs sm:text-sm">
                                            {formatDatetime(entry.datetime)}
                                        </td>
                                        <td className="border p-2 text-center text-xs sm:text-sm">
                                            {currencyAdapter(entry.totalInvestedAmount, investment.currency)}
                                        </td>
                                        <td className="border p-2 text-center text-xs sm:text-sm">
                                            {(entry.profitability * 100).toFixed(2)} %
                                        </td>
                                        <td className="border p-2 text-center text-xs sm:text-sm">
                                            {entry.comments || "-"}
                                        </td>
                                        <td className="border p-2 text-center">
                                            <button
                                                className="px-2 py-1 bg-red-500 text-white text-xs rounded hover:bg-red-600 transition duration-200"
                                                onClick={() => setIsConfirmingDelete(entry)}
                                            >
                                                Delete
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="6" className="text-center p-4 text-gray-500">
                                        No entries available
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>

                {isCreating && (
                    <CreateEntryModal
                        investment={investment}
                        onClose={() => setIsCreating(false)}
                        onCreate={reload}
                    />
                )}

                {isConfirmingDelete && (
                    <ConfirmDeleteModal
                        entity="entry"
                        id={isConfirmingDelete.id}
                        name={isConfirmingDelete.id}
                        onCancel={() => setIsConfirmingDelete(null)}
                        onConfirm={() => { setIsConfirmingDelete(null); handleDeleteEntry(isConfirmingDelete.id) }}
                    />
                )}

            {showGraphModal && (
                <InvestmentGraphModal
                    investment={investment}
                    onClose={() => setShowGraphModal(false)}
                    showDetailsButton={false}
                />
            )}
        </div>
    );
}