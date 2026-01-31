import { currencyAdapter } from "@/lib/currencyAdapter";
import { formatDate } from "@/lib/datetimeFormater";

export default function InvestmentListItem({ investment, onGraph, onForecast, onDetails, onDelete }) {
    const lastEntry = investment.lastEntry;
    return (
        <li
            className="group bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300 border border-slate-200 p-4 sm:p-6 flex flex-col sm:flex-row gap-4 lg:gap-6 items-stretch font-sans"
        >
            {/* Name & Description */}
            <div className="flex-1 flex flex-col justify-center text-center sm:text-left border-b sm:border-b-0 sm:border-r border-gray-100 pr-0 sm:pr-6 mb-4 sm:mb-0">
                <h3 className="text-base sm:text-lg font-semibold text-gray-700">{investment.name}</h3>
                <p className="text-gray-600 text-xs sm:text-sm">{investment.description} ({investment.currency})</p>
                <p className="text-gray-500 text-xs mt-1">
                    {formatDate(investment.startDateTime)} - {investment.endDateTime ? formatDate(investment.endDateTime) : 'Present'}
                </p>
            </div>
            {/* Profitability & Obtained & Benefit */}
            <div className="flex-1 flex flex-col justify-center items-center sm:items-end text-center sm:text-right border-b sm:border-b-0 sm:border-r border-gray-100 px-0 sm:px-6 mb-4 sm:mb-0">
                <p className="text-xs sm:text-sm text-gray-500">
                    Profit: <span className="font-medium text-green-600">{lastEntry?.profitability != null ? (100 * lastEntry.profitability).toFixed(2) : "-"}%</span>
                </p>
                <p className="text-xs sm:text-sm text-gray-500">
                    Obtained: <span className="font-medium text-blue-600">{lastEntry?.obtained != null ? currencyAdapter(lastEntry.obtained, investment.currency) : "-"}</span>
                </p>
                <p className="text-xs sm:text-sm text-gray-500">
                    Benefit: <span className="font-medium text-purple-600">{lastEntry?.benefit != null ? currencyAdapter(lastEntry.benefit, investment.currency) : "-"}</span>
                </p>
            </div>
            {/* Action Buttons in Dedicated Grid Cell */}
            <div className="flex flex-col sm:flex-row gap-2 justify-end flex-1 items-center sm:items-center pl-0 sm:pl-6">
                <button
                    className="px-3 py-2 text-xs sm:text-sm font-medium bg-gray-100 text-gray-500 border border-gray-200 rounded hover:bg-gray-200 transition duration-200"
                    onClick={onGraph}
                >
                    Graph
                </button>
                <button
                    className="px-3 py-2 text-xs sm:text-sm font-medium bg-gray-100 text-gray-500 border border-gray-200 rounded hover:bg-gray-200 transition duration-200"
                    onClick={onForecast}
                >
                    Forecast
                </button>
                <button
                    className="px-3 py-2 text-xs sm:text-sm font-semibold bg-blue-600 text-white border border-blue-700 rounded hover:bg-blue-700 transition duration-200 shadow"
                    onClick={onDetails}
                >
                    Details
                </button>
                <button
                    className="px-3 py-2 text-xs sm:text-sm font-semibold bg-red-500 text-white border border-red-700 rounded hover:bg-red-600 transition duration-200 shadow"
                    onClick={onDelete}
                >
                    Delete
                </button>
            </div>
        </li>
    );
}
