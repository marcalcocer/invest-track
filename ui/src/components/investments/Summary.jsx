import { useState } from "react";
import { formatValueForPrivacy } from "@/lib/currencyAdapter";
import { WalletIcon, BanknotesIcon, ArrowTrendingUpIcon, ChartBarIcon, ChevronDownIcon, ChevronUpIcon } from "@heroicons/react/24/solid";

const SummaryRow = ({ invested, obtained, benefit, profit, title, isPrivate }) => (
  <div className="mb-8 last:mb-0">
    {title && (
      <h3 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-4 text-left ml-1">
        {title}
      </h3>
    )}
    <dl className="grid grid-cols-2 gap-3 sm:gap-4 lg:grid-cols-4">
      {/* Invested */}
      <div className="flex flex-col items-center rounded-lg border border-gray-200 bg-gray-50 px-2 py-3 sm:px-3 sm:py-4 text-center">
        <WalletIcon className="w-6 h-6 text-gray-400 mb-1" />
        <dd className="text-base sm:text-lg font-bold text-gray-700">
          {formatValueForPrivacy(invested, "EUR", isPrivate)}
        </dd>
        <dt className="order-last text-xs sm:text-sm font-medium text-gray-400 mt-1">
          Invested
        </dt>
      </div>

      {/* Obtained */}
      <div className="flex flex-col items-center rounded-lg border border-gray-200 bg-gray-50 px-2 py-3 sm:px-3 sm:py-4 text-center">
        <BanknotesIcon className="w-6 h-6 text-gray-400 mb-1" />
        <dd className="text-base sm:text-lg font-bold text-gray-700">
          {formatValueForPrivacy(obtained, "EUR", isPrivate)}
        </dd>
        <dt className="order-last text-xs sm:text-sm font-medium text-gray-500 mt-1">
          Obtained
        </dt>
      </div>

      {/* Benefit */}
      <div className="flex flex-col items-center rounded-lg border border-gray-200 bg-gray-50 px-2 py-3 sm:px-3 sm:py-4 text-center">
        <ChartBarIcon className="w-6 h-6 text-gray-400 mb-1" />
        <dd className="text-base sm:text-lg font-bold text-gray-700">
          {formatValueForPrivacy(benefit, "EUR", isPrivate)}
        </dd>
        <dt className="order-last text-xs sm:text-sm font-medium text-gray-400 mt-1">
          Benefit
        </dt>
      </div>

      {/* Profit */}
      <div className="flex flex-col items-center rounded-lg border border-gray-200 bg-gray-50 px-2 py-3 sm:px-3 sm:py-4 text-center">
        <ArrowTrendingUpIcon className="w-6 h-6 text-gray-400 mb-1" />
        <dd className="text-base sm:text-lg font-bold text-gray-700 flex items-center gap-1 uppercase">
          {(100 * profit).toFixed(2)}%
        </dd>
        <dt className="order-last text-xs sm:text-sm font-medium text-gray-500 mt-1">
          Profit
        </dt>
      </div>
    </dl>
  </div>
);

export default function InvestmentsSummary({ summary, isPrivate }) {
  const [isOrganicExpanded, setIsOrganicExpanded] = useState(false);

  return (
    <section className="bg-white rounded-2xl shadow-lg hover:shadow-xl transition-shadow duration-300 p-8 mb-6 mx-auto max-w-screen-lg font-sans">
      <div className="mx-auto max-w-2xl text-center">
        <h2 className="text-xl font-bold text-gray-900 sm:text-2xl lg:text-3xl mb-8 text-center">
          Summary
        </h2>
      </div>

      <SummaryRow 
        title="Total (Including Reinvested)"
        invested={summary.investedAmount}
        obtained={summary.obtained}
        benefit={summary.benefit}
        profit={summary.profitability}
        isPrivate={isPrivate}
      />

      <div className="mt-8 border-t border-gray-100">
        <button 
          onClick={() => setIsOrganicExpanded(!isOrganicExpanded)}
          className="w-full py-4 flex items-center justify-between text-gray-500 hover:text-gray-700 transition-colors duration-200 group"
        >
          <span className="text-xs font-semibold uppercase tracking-wider">
            Organic Details (Initial Investment Only)
          </span>
          {isOrganicExpanded ? (
            <ChevronUpIcon className="w-5 h-5 group-hover:-translate-y-1 transition-transform duration-200" />
          ) : (
            <ChevronDownIcon className="w-5 h-5 group-hover:translate-y-1 transition-transform duration-200" />
          )}
        </button>
      </div>

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
