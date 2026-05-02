import { useEffect, useState } from "react";
import { InvestmentService } from "@/lib/InvestmentService";
import { ForecastService } from "@/lib/ForecastService";
import { numberAdapter } from "@/lib/currencyAdapter";
import CreateEntryModal from "@/components/modals/CreateEntryModal";
import LoadingSpinner from "@/components/LoadingSpinner";
import { formatDatetime } from "@/lib/datetimeFormater";
import ConfirmDeleteModal from "@/components/modals/ConfirmDeleteModal";
import UpdateEntryModal from "@/components/modals/UpdateEntryModal";
import InvestmentGraphModal from "../modals/InvestmentGraphModal";
import ForecastGraphModal from "../modals/ForecastGraphModal";
import CreateForecastModal from "../modals/CreateForecastModal";
import EditForecastModal from "../modals/EditForecastModal";
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
    const [isUpdatingEntry, setIsUpdatingEntry] = useState(null);
    // Forecasts state
    const [forecasts, setForecasts] = useState([]);
    const [isLoadingForecasts, setIsLoadingForecasts] = useState(true);
    const [isConfirmingDeleteForecast, setIsConfirmingDeleteForecast] = useState(null);
    const [editingForecast, setEditingForecast] = useState(null);
    const [selectedForecastForGraph, setSelectedForecastForGraph] = useState(null);

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
                ForecastService.fetchForecasts(inv.id)
                    .then((forecastsData) => {
                        // Attach entries to each forecast for start date calculation
                        const forecastsWithEntries = forecastsData.map(f => ({ ...f, entriesFromParent: inv.entries }));
                        setForecasts(forecastsWithEntries);
                    })
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
            await ForecastService.createForecast(investment.id, forecastData);
            window.location.reload();
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
            await ForecastService.deleteForecast(investment.id, forecastId);
            window.location.reload();
        } catch (e) {
            // Optionally show error
        } finally {
            setIsLoadingForecasts(false);
        }
    };

    const handleEditForecast = (forecast) => {
        setEditingForecast(forecast);
    };

    const handleUpdateForecast = () => {
        setEditingForecast(null);
        window.location.reload();
    };

    return (
        <div className="p-4 sm:p-6 max-w-5xl mx-auto">
            {isDeleting && <LoadingSpinner />}
            {showForecastModal && (
                <CreateForecastModal
                    investment={investment}
                    entries={entries}
                    onClose={() => setShowForecastModal(false)}
                    onCreate={handleCreateForecast}
                />
            )}
            {editingForecast && (
                <EditForecastModal
                    investment={investment}
                    forecast={editingForecast}
                    entries={entries}
                    onClose={() => setEditingForecast(null)}
                    onUpdate={handleUpdateForecast}
                />
            )}
            {selectedForecastForGraph && (
                <ForecastGraphModal
                    forecast={selectedForecastForGraph}
                    entries={entries}
                    investment={investment}
                    onClose={() => setSelectedForecastForGraph(null)}
                />
            )}
            

                <h2 className="text-lg sm:text-xl font-bold mb-3 sm:mb-4">{investment.name}</h2>
                <p className="text-gray-600 text-sm sm:text-base mb-4">
                    {investment.description} ({investment.currency})
                </p>
                {/* Mini-summary */}
                {Array.isArray(entries) && entries.length > 0 && (
                <div className="flex flex-wrap gap-4 items-center justify-center mb-4 text-xs sm:text-sm text-gray-700 bg-gray-50 rounded-lg px-3 py-2 border border-gray-100">
                    <span>Entries: <span className="font-semibold">{entries.length}</span></span>
                    <span>Avg Profit: <span className="font-semibold">{numberAdapter(entries.reduce((acc, e) => acc + (e.profitability ?? 0), 0) / entries.length * 100)}%</span></span>
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
                        Create Forecast
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
                    key={forecasts.map(f => f.id).join('-')}
                    forecasts={forecasts}
                    isLoadingForecasts={isLoadingForecasts}
                    onDeleteForecast={handleDeleteForecast}
                    isConfirmingDeleteForecast={isConfirmingDeleteForecast}
                    setIsConfirmingDeleteForecast={setIsConfirmingDeleteForecast}
                    onEditForecast={handleEditForecast}
                    onViewGraph={setSelectedForecastForGraph}
                />

                {/* Mobile Cards View */}
                <InvestmentEntriesMobile
                    entries={entries}
                    investment={investment}
                    setIsConfirmingDelete={setIsConfirmingDelete}
                />

                <h3 className="text-md sm:text-lg font-semibold mb-2 text-center mt-8">Entries</h3>

                {/* Desktop Table View */}
                <InvestmentEntriesTable
                    entries={entries}
                    investment={investment}
                    setIsConfirmingDelete={setIsConfirmingDelete}
                    setIsUpdatingEntry={setIsUpdatingEntry}
                />

                {isCreating && (
                    <CreateEntryModal
                        investment={investment}
                        onClose={() => setIsCreating(false)}
                        onCreate={reload}
                    />
                )}

                {isUpdatingEntry && (
                    <UpdateEntryModal
                        entry={isUpdatingEntry}
                        onClose={() => setIsUpdatingEntry(null)}
                        onUpdate={async (updatedEntry) => {
                            // Debug: log ids
                            console.log('UpdateEntryModal onUpdate', { updatedEntry, isUpdatingEntry });
                            const entryId = updatedEntry.id || isUpdatingEntry?.id;
                            if (!entryId) {
                                alert('Entry ID is missing!');
                                return;
                            }
                            await InvestmentService.updateInvestmentEntry(investment.id, entryId, updatedEntry);
                            setIsUpdatingEntry(null);
                            reload();
                        }}
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