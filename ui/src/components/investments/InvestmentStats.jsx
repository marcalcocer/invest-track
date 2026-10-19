import { currencyAdapter, numberAdapter, formatValueForPrivacy } from "@/lib/currencyAdapter";

export default function InvestmentStats({ investment, currency, isPrivate }) {
    if (!investment?.lastEntry) return null;
    
    return (
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 mt-3">
            <div className="text-center">
                <p className="text-xs text-muted-foreground">Initial</p>
                <p className="text-xs font-semibold text-foreground">
                    {formatValueForPrivacy(investment.lastEntry.initialInvestedAmount, currency, isPrivate)}
                </p>
            </div>
            <div className="text-center">
                <p className="text-xs text-muted-foreground">Total</p>
                <p className="text-xs font-semibold text-foreground">
                    {formatValueForPrivacy(investment.lastEntry.totalInvestedAmount, currency, isPrivate)}
                </p>
            </div>
            <div className="text-center">
                <p className="text-xs text-muted-foreground">Obtained</p>
                <p className="text-xs font-semibold text-foreground">
                    {formatValueForPrivacy(investment.lastEntry.obtained, currency, isPrivate)}
                </p>
            </div>
            <div className="text-center">
                <p className="text-xs text-muted-foreground">Profit</p>
                <p className="text-xs font-semibold text-foreground">
                    {numberAdapter(100 * investment.lastEntry.profitability) + "%"}
                </p>
            </div>
        </div>
    );
}
