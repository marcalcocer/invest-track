import { currencyAdapter } from "@/lib/currencyAdapter";
import { WalletIcon, BanknotesIcon, ArrowTrendingUpIcon, ChartBarIcon } from "@heroicons/react/24/solid";

export default function InvestmentsSummary({ summary }) {
  return (
    <section className="bg-white rounded-2xl shadow-lg hover:shadow-xl transition-shadow duration-300 p-8 mb-6 mx-auto max-w-screen-lg font-sans">
      <div className="mx-auto max-w-2xl text-center">
        <h2 className="text-xl font-bold text-gray-900 sm:text-2xl lg:text-3xl mb-6 text-center">
          Summary
        </h2>
      </div>

      <dl className="mt-6 sm:mt-8 lg:mt-12 grid grid-cols-2 gap-3 sm:gap-4 lg:grid-cols-4">
        {/* Invested */}
        <div className="flex flex-col items-center rounded-lg border border-gray-200 bg-gray-50 px-2 py-3 sm:px-3 sm:py-4 text-center">
          <WalletIcon className="w-6 h-6 text-gray-400 mb-1" />
          <dd className="text-base sm:text-lg font-bold text-gray-700">
            {currencyAdapter(summary.investedAmount)}
          </dd>
          <dt className="order-last text-xs sm:text-sm font-medium text-gray-400 mt-1">
            Invested
          </dt>
        </div>

        {/* Obtained */}
        <div className="flex flex-col items-center rounded-lg border border-gray-200 bg-gray-50 px-2 py-3 sm:px-3 sm:py-4 text-center">
          <BanknotesIcon className="w-6 h-6 text-gray-400 mb-1" />
          <dd className="text-base sm:text-lg font-bold text-gray-700">
            {currencyAdapter(summary.obtained)}
          </dd>
          <dt className="order-last text-xs sm:text-sm font-medium text-gray-500 mt-1">
            Obtained
          </dt>
        </div>

        {/* Benefit */}
        <div className="flex flex-col items-center rounded-lg border border-gray-200 bg-gray-50 px-2 py-3 sm:px-3 sm:py-4 text-center">
          <ChartBarIcon className="w-6 h-6 text-gray-400 mb-1" />
          <dd className="text-base sm:text-lg font-bold text-gray-700">
            {currencyAdapter(summary.benefit)}
          </dd>
          <dt className="order-last text-xs sm:text-sm font-medium text-gray-400 mt-1">
            Benefit
          </dt>
        </div>

        {/* Profit */}
        <div className="flex flex-col items-center rounded-lg border border-gray-200 bg-gray-50 px-2 py-3 sm:px-3 sm:py-4 text-center">
          <ArrowTrendingUpIcon className="w-6 h-6 text-gray-400 mb-1" />
          <dd className="text-base sm:text-lg font-bold text-gray-700 flex items-center gap-1 uppercase">
            {(100 * summary.profitability).toFixed(2)}%
          </dd>
          <dt className="order-last text-xs sm:text-sm font-medium text-gray-500 mt-1">
            Profit
          </dt>
        </div>
      </dl>
    </section>
  );
}