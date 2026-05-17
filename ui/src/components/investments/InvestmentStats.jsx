import { currencyAdapter, numberAdapter, formatValueForPrivacy } from "@/lib/currencyAdapter";

export default function InvestmentStats({ investment, currency, isPrivate }) {
    if (!investment?.lastEntry) return null;
    
    return (
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 mt-3">
            <div className="text-center">
                <p className="text-xs text-gray-500">Initial</p>
                <p className="text-xs font-semibold text-gray-700">
                    {formatValueForPrivacy(investment.lastEntry.initialInvestedAmount, currency, isPrivate)}
                </p>
            </div>
            <div className="text-center">
                <p className="text-xs text-gray-500">Total</p>
                <p className="text-xs font-semibold text-gray-700">
                    {formatValueForPrivacy(investment.lastEntry.totalInvestedAmount, currency, isPrivate)}
                </p>
            </div>
            <div className="text-center">
                <p className="text-xs text-gray-500">Obtained</p>
                <p className="text-xs font-semibold text-gray-700">
                    {formatValueForPrivacy(investment.lastEntry.obtained, currency, isPrivate)}
                </p>
            </div>
            <div className="text-center">
                <p className="text-xs text-gray-500">Profit</p>
                <p className="text-xs font-semibold text-gray-700">
                    {numberAdapter(100 * investment.lastEntry.profitability) + "%"}
                </p>
            </div>
        </div>
    );
}
