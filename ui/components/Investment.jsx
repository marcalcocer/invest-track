import { useEffect, useState } from "react";
import { InvestmentService } from "@/lib/InvestmentService";
import { currencyAdapter } from "@/lib/currencyAdapter";
import CreateEntryModal from "@/components/modals/CreateEntryModal";
import LoadingSpinner from "@/components/LoadingSpinner";
import { formatDatetime } from "@/lib/datetimeFormater";
import ConfirmDeleteModal from "@/components/modals/ConfirmDeleteModal";
import InvestmentGraphModal from "../modals/InvestmentGraphModal";

export default function Investment() {
    const [investment, setInvestment] = useState(null);
    const [entries, setEntries] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isCreating, setIsCreating] = useState(false);
    const [isConfirmingDelete, setIsConfirmingDelete] = useState(null);
    const [isDeleting, setIsDeleting] = useState(null);
    const [showGraphModal, setShowGraphModal] = useState(false);

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

    return (
        <>
            {isDeleting && <LoadingSpinner />}

            <div className="p-4 sm:p-6 max-w-3xl mx-auto">
                <h2 className="text-lg sm:text-xl font-bold mb-3 sm:mb-4">{investment.name} - Entries</h2>
                <p className="text-gray-600 text-sm sm:text-base mb-4">
                    {investment.description} ({investment.currency})
                </p>

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
                        className="px-3 py-2 sm:px-4 sm:py-2 bg-gray-500 text-white text-sm rounded shadow hover:bg-gray-600 transition duration-200"
                        onClick={() => window.history.back()}
                    >
                        Back
                    </button>
                </div>

                {/* Mobile Cards View */}
                <div className="block sm:hidden mt-4 space-y-3">
                    {entries.length > 0 ? (
                        entries.map((entry) => (
                            <div key={entry.id} className="bg-white border border-gray-200 rounded-lg p-3 shadow-sm">
                                <div className="grid grid-cols-2 gap-2 text-sm">
                                    <div>
                                        <span className="font-medium text-gray-500">ID:</span>
                                        <p className="text-gray-800">{entry.id}</p>
                                    </div>
                                    <div>
                                        <span className="font-medium text-gray-500">Date:</span>
                                        <p className="text-gray-800">{formatDatetime(entry.datetime)}</p>
                                    </div>
                                    <div>
                                        <span className="font-medium text-gray-500">Total Invested:</span>
                                        <p className="text-gray-800">{currencyAdapter(entry.totalInvestedAmount, investment.currency)}</p>
                                    </div>
                                    <div>
                                        <span className="font-medium text-gray-500">Profitability:</span>
                                        <p className="text-gray-800">{(entry.profitability * 100).toFixed(2)}%</p>
                                    </div>
                                    <div className="col-span-2">
                                        <span className="font-medium text-gray-500">Comments:</span>
                                        <p className="text-gray-800 truncate">{entry.comments || "-"}</p>
                                    </div>
                                </div>
                                <div className="mt-3 flex justify-end">
                                    <button
                                        className="px-3 py-1 bg-red-500 text-white text-xs rounded hover:bg-red-600 transition duration-200"
                                        onClick={() => setIsConfirmingDelete(entry)}
                                    >
                                        Delete
                                    </button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="text-center p-4 text-gray-500 bg-white border border-gray-200 rounded-lg">
                            No entries available
                        </div>
                    )}
                </div>

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
                                entries.map((entry) => (
                                    <tr key={entry.id} className="border">
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
            </div>

            {showGraphModal && (
                <InvestmentGraphModal
                    investment={investment}
                    onClose={() => setShowGraphModal(false)}
                    showDetailsButton={false}
                />
            )}
        </>
    );
}