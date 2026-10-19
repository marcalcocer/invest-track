import { useState } from "react";
import { formatValueForPrivacy, formatPercentForPrivacy } from "@/lib/currencyAdapter";
import { cn } from "@/lib/cn";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import Badge from "@/components/ui/Badge";
import { WalletIcon, BanknotesIcon, ArrowTrendingUpIcon, ChartBarIcon, ChevronDownIcon } from "@heroicons/react/24/outline";

const signClass = (value) => {
  if (value > 0) return "text-success";
  if (value < 0) return "text-danger";
  return "text-foreground";
};

const signVariant = (value) => {
  if (value > 0) return "success";
  if (value < 0) return "danger";
  return "neutral";
};

const KpiCard = ({ label, icon: Icon, value, valueClassName, badge }) => (
  <Card>
    <CardHeader>
      <CardTitle>{label}</CardTitle>
      <Icon className="w-5 h-5 text-muted-foreground" aria-hidden="true" />
    </CardHeader>
    <CardContent>
      <p className={cn("text-xl sm:text-2xl font-semibold tabular-nums tracking-tight", valueClassName)}>
        {value}
      </p>
      {badge}
    </CardContent>
  </Card>
);

const SummaryRow = ({ invested, obtained, benefit, profit, title, isPrivate }) => (
  <div className="mb-6 last:mb-0">
    {title && (
      <h3 className="text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-3">
        {title}
      </h3>
    )}
    <div className="grid grid-cols-1 min-[480px]:grid-cols-2 gap-3 sm:gap-4 lg:grid-cols-4">
      <KpiCard
        label="Invested"
        icon={WalletIcon}
        value={formatValueForPrivacy(invested, "EUR", isPrivate)}
      />
      <KpiCard
        label="Obtained"
        icon={BanknotesIcon}
        value={formatValueForPrivacy(obtained, "EUR", isPrivate)}
      />
      <KpiCard
        label="Benefit"
        icon={ChartBarIcon}
        value={formatValueForPrivacy(benefit, "EUR", isPrivate)}
        valueClassName={signClass(benefit)}
      />
      <KpiCard
        label="Profit"
        icon={ArrowTrendingUpIcon}
        value={formatPercentForPrivacy(profit, isPrivate)}
        valueClassName={signClass(profit)}
        badge={
          !isPrivate && (
            <Badge variant={signVariant(profit)} className="mt-2">
              {profit > 0 ? "Gain" : profit < 0 ? "Loss" : "Break-even"}
            </Badge>
          )
        }
      />
    </div>
  </div>
);

export default function InvestmentsSummary({ summary, isPrivate }) {
  const [isOrganicExpanded, setIsOrganicExpanded] = useState(false);

  return (
    <section className="mb-8">
      <h2 className="text-xl font-semibold tracking-tight sm:text-2xl mb-5">Summary</h2>

      <SummaryRow
        title="Total (Including Reinvested)"
        invested={summary.investedAmount}
        obtained={summary.obtained}
        benefit={summary.benefit}
        profit={summary.profitability}
        isPrivate={isPrivate}
      />

      <button
        type="button"
        onClick={() => setIsOrganicExpanded(!isOrganicExpanded)}
        aria-expanded={isOrganicExpanded}
        className="w-full py-3 flex items-center justify-between border-t border-border text-muted-foreground hover:text-foreground transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring rounded-md"
      >
        <span className="text-xs font-semibold uppercase tracking-wider">
          Organic Details (Initial Investment Only)
        </span>
        <ChevronDownIcon
          className={cn("w-5 h-5 transition-transform duration-200", isOrganicExpanded && "rotate-180")}
        />
      </button>

      {isOrganicExpanded && (
        <div className="pt-2 animate-fadeIn">
          <SummaryRow
            invested={summary.initialInvestedAmount}
            obtained={summary.initialObtained}
            benefit={summary.initialBenefit}
            profit={summary.initialProfitability}
            isPrivate={isPrivate}
          />
        </div>
      )}
    </section>
  );
}
