import { currencyAdapter } from "@/lib/currencyAdapter";
import { formatDatetime } from "@/lib/datetimeFormater";

export default function InvestmentEntriesMobile({ entries, investment, setIsConfirmingDelete }) {
    return (
        <div className="block sm:hidden mt-4 space-y-3">
            {Array.isArray(entries) && entries.length > 0 ? (
                entries.map((entry, idx) => (
                    <div key={entry.id ?? `entry-${idx}`} className="bg-white rounded-xl shadow-sm border border-slate-200 p-4">
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
                <div className="text-center p-4 text-gray-500 bg-white border border-slate-200 rounded-xl shadow-sm">
                    No entries available
                </div>
            )}
        </div>
    );
}
