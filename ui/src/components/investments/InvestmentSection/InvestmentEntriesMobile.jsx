import { formatValueForPrivacy } from "@/lib/currencyAdapter";
import { formatDatetime } from "@/lib/datetimeFormater";

export default function InvestmentEntriesMobile({ entries, investment, setIsConfirmingDelete, isPrivate }) {
    return (
        <div className="block sm:hidden mt-4 space-y-3">
            {Array.isArray(entries) && entries.length > 0 ? (
                entries.map((entry, idx) => (
                    <div key={entry.id ?? `entry-${idx}`} className="bg-card text-card-foreground rounded-xl shadow-sm border border-border p-4">
                        <div className="grid grid-cols-2 gap-2 text-sm">
                            <div>
                                <span className="font-medium text-muted-foreground">ID:</span>
                                <p className="text-foreground">{entry.id}</p>
                            </div>
                            <div>
                                <span className="font-medium text-muted-foreground">Date:</span>
                                <p className="text-foreground">{formatDatetime(entry.datetime)}</p>
                            </div>
                            <div>
                                <span className="font-medium text-muted-foreground">Total Invested:</span>
                                <p className="text-foreground">{formatValueForPrivacy(entry.totalInvestedAmount, investment.currency, isPrivate)}</p>
                            </div>
                            <div>
                                <span className="font-medium text-muted-foreground">Profitability:</span>
                                <p className="text-foreground">{(entry.profitability * 100).toFixed(2)}%</p>
                            </div>
                            <div className="col-span-2">
                                <span className="font-medium text-muted-foreground">Comments:</span>
                                <p className="text-foreground truncate">{entry.comments || "-"}</p>
                            </div>
                        </div>
                        <div className="mt-3 flex justify-end">
                            <button
                                className="px-3 py-1 bg-danger text-white text-xs rounded hover:opacity-90 transition duration-200"
                                onClick={() => setIsConfirmingDelete(entry)}
                            >
                                Delete
                            </button>
                        </div>
                    </div>
                ))
            ) : (
                <div className="text-center p-4 text-muted-foreground bg-card border border-border rounded-xl shadow-sm">
                    No entries available
                </div>
            )}
        </div>
    );
}
