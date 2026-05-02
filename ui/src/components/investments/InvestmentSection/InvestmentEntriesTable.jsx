import { currencyAdapter } from "@/lib/currencyAdapter";
import { formatDatetime } from "@/lib/datetimeFormater";

export default function InvestmentEntriesTable({
    entries,
    investment,
    setIsConfirmingDelete,
    setIsUpdatingEntry
}) {
    return (
        <div className="hidden sm:block mt-6">
            <table className="mx-auto border-collapse border border-gray-200">
                <thead>
                    <tr className="bg-gray-100">
                        <th className="border p-2 text-xs sm:text-sm whitespace-nowrap">ID</th>
                        <th className="border p-2 text-xs sm:text-sm whitespace-nowrap">Date</th>
                        <th className="border p-2 text-xs sm:text-sm whitespace-nowrap">Total Invested</th>
                        <th className="border p-2 text-xs sm:text-sm whitespace-nowrap">Profitability</th>
                        <th className="border p-2 text-xs sm:text-sm whitespace-nowrap">Comments</th>
                        <th className="border p-2 text-xs sm:text-sm whitespace-nowrap">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {entries.length > 0 ? (
                        entries.map((entry, idx) => (
                            <tr
                                key={entry.id}
                                className={`border transition-colors ${idx % 2 === 1 ? 'bg-slate-50' : ''} hover:bg-slate-100`}
                            >
                                <td className="border p-2 text-center text-xs sm:text-sm whitespace-nowrap">{entry.id}</td>
                                <td className="border p-2 text-center text-xs sm:text-sm whitespace-nowrap">
                                    {formatDatetime(entry.datetime)}
                                </td>
                                <td className="border p-2 text-center text-xs sm:text-sm whitespace-nowrap">
                                    {currencyAdapter(entry.totalInvestedAmount, investment.currency)}
                                </td>
                                <td className="border p-2 text-center text-xs sm:text-sm whitespace-nowrap">
                                    {(entry.profitability * 100).toFixed(2)} %
                                </td>
                                <td className="border p-2 text-center text-xs sm:text-sm whitespace-nowrap">
                                    {entry.comments || "-"}
                                </td>
                                <td className="border p-2 text-center whitespace-nowrap">
                                    <div className="flex flex-row gap-2 justify-center items-center">
                                        <button
                                            className="px-2 py-1 bg-blue-500 text-white text-xs rounded hover:bg-blue-600 transition duration-200"
                                            onClick={() => setIsUpdatingEntry(entry)}
                                        >
                                            Edit
                                        </button>
                                        <button
                                            className="px-2 py-1 bg-red-500 text-white text-xs rounded hover:bg-red-600 transition duration-200"
                                            onClick={() => setIsConfirmingDelete(entry)}
                                        >
                                            Delete
                                        </button>
                                    </div>
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
    );
}
