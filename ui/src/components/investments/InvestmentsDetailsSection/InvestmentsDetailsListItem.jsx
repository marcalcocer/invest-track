import { currencyAdapter, formatValueForPrivacy } from "@/lib/currencyAdapter";
import { formatDate } from "@/lib/datetimeFormater";

export default function InvestmentListItem({ investment, onGraph, onForecast, onDetails, onDelete, onEdit, isPrivate }) {
    const lastEntry = investment.lastEntry;
    return (
        <li
            className="group bg-card text-card-foreground rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 border border-border p-4 sm:p-6 flex flex-col sm:flex-row gap-4 lg:gap-6 items-stretch font-sans"
        >
            {/* Name & Description */}
            <div className="flex-1 flex flex-col justify-center text-center sm:text-left border-b sm:border-b-0 sm:border-r border-border pr-0 sm:pr-6 mb-4 sm:mb-0">
                <h3 className="text-base sm:text-lg font-semibold text-foreground">{investment.name}</h3>
                <p className="text-muted-foreground text-xs sm:text-sm">{investment.description} ({investment.currency})</p>
                <p className="text-muted-foreground text-xs mt-1">
                    {formatDate(investment.startDateTime)} - {investment.endDateTime ? formatDate(investment.endDateTime) : 'Present'}
                </p>
            </div>
            {/* Profitability & Obtained & Benefit */}
            <div className="flex-1 flex flex-col justify-center items-center sm:items-end text-center sm:text-right border-b sm:border-b-0 sm:border-r border-border px-0 sm:px-6 mb-4 sm:mb-0">
                <p className="text-xs sm:text-sm text-muted-foreground">
                    Profit: <span className="font-medium text-success">{lastEntry?.profitability != null ? (100 * lastEntry.profitability).toFixed(2) : "-"}%</span>
                </p>
                <p className="text-xs sm:text-sm text-muted-foreground">
                    Obtained: <span className="font-medium text-primary">{formatValueForPrivacy(lastEntry?.obtained, investment.currency, isPrivate)}</span>
                </p>
                <p className="text-xs sm:text-sm text-muted-foreground">
                    Benefit: <span className="font-medium text-foreground">{formatValueForPrivacy(lastEntry?.benefit, investment.currency, isPrivate)}</span>
                </p>
            </div>
            {/* Action Buttons in Dedicated Grid Cell */}
            <div className="flex flex-col sm:flex-row gap-2 justify-end flex-1 items-center sm:items-center pl-0 sm:pl-6">
                <button
                    className="px-3 py-2 text-xs sm:text-sm font-medium bg-muted text-foreground border border-border rounded hover:bg-muted/70 transition duration-200"
                    onClick={onGraph}
                >
                    Graph
                </button>
                <button
                    className="px-3 py-2 text-xs sm:text-sm font-medium bg-muted text-foreground border border-border rounded hover:bg-muted/70 transition duration-200"
                    onClick={onForecast}
                >
                    Forecast
                </button>
                <button
                    className="px-3 py-2 text-xs sm:text-sm font-semibold bg-primary text-primary-foreground border border-primary rounded hover:opacity-90 transition duration-200 shadow"
                    onClick={onDetails}
                >
                    Details
                </button>
                <button
                    className="px-3 py-2 text-xs sm:text-sm font-semibold bg-muted text-foreground border border-border rounded hover:bg-muted/70 transition duration-200 shadow"
                    onClick={onEdit}
                >
                    Edit
                </button>
                <button
                    className="px-3 py-2 text-xs sm:text-sm font-semibold bg-danger text-white border border-danger rounded hover:opacity-90 transition duration-200 shadow"
                    onClick={onDelete}
                >
                    Delete
                </button>
            </div>
        </li>
    );
}
