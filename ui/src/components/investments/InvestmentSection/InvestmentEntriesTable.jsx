import { formatValueForPrivacy } from "@/lib/currencyAdapter";
import { formatDatetime } from "@/lib/datetimeFormater";

export default function InvestmentEntriesTable({
    entries,
    investment,
    setIsConfirmingDelete,
    setIsUpdatingEntry,
    isPrivate
}) {
    return (
        <div className="hidden sm:block mt-6">
            <table className="mx-auto border-collapse border border-border text-foreground">
                <thead>
                    <tr className="bg-muted text-foreground">
                        <th className="border border-border p-2 text-xs sm:text-sm whitespace-nowrap">ID</th>
                        <th className="border border-border p-2 text-xs sm:text-sm whitespace-nowrap">Date</th>
                        <th className="border border-border p-2 text-xs sm:text-sm whitespace-nowrap">Total Invested</th>
                        <th className="border border-border p-2 text-xs sm:text-sm whitespace-nowrap">Profitability</th>
                        <th className="border border-border p-2 text-xs sm:text-sm whitespace-nowrap">Comments</th>
                        <th className="border border-border p-2 text-xs sm:text-sm whitespace-nowrap">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {entries.length > 0 ? (
                        entries.map((entry, idx) => (
                            <tr
                                key={entry.id}
                                className={`border border-border transition-colors ${idx % 2 === 1 ? 'bg-muted/50' : 'bg-card'} hover:bg-muted`}
                            >
                                <td className="border border-border p-2 text-center text-xs sm:text-sm whitespace-nowrap">{entry.id}</td>
                                <td className="border border-border p-2 text-center text-xs sm:text-sm whitespace-nowrap">
                                    {formatDatetime(entry.datetime)}
                                </td>
                                <td className="border border-border p-2 text-center text-xs sm:text-sm whitespace-nowrap">
                                    {formatValueForPrivacy(entry.totalInvestedAmount, investment.currency, isPrivate)}
                                </td>
                                <td className="border border-border p-2 text-center text-xs sm:text-sm whitespace-nowrap">
                                    {(entry.profitability * 100).toFixed(2)} %
                                </td>
                                <td className="border border-border p-2 text-center text-xs sm:text-sm whitespace-nowrap">
                                    {entry.comments || "-"}
                                </td>
                                <td className="border border-border p-2 text-center whitespace-nowrap">
                                    <div className="flex flex-row gap-2 justify-center items-center">
                                        <button
                                            className="px-2 py-1 bg-primary text-primary-foreground text-xs rounded hover:opacity-90 transition duration-200"
                                            onClick={() => setIsUpdatingEntry(entry)}
                                        >
                                            Edit
                                        </button>
                                        <button
                                            className="px-2 py-1 bg-danger text-white text-xs rounded hover:opacity-90 transition duration-200"
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
                            <td colSpan="6" className="text-center p-4 text-muted-foreground">
                                No entries available
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    );
}
