import { currencyAdapter } from "@/lib/currencyAdapter";

export default function InvestmentsSummary({ summary }) {
  return (
    <div className="mx-auto max-w-screen-lg px-3 py-4 sm:px-4 sm:py-6 lg:px-8">
      <div className="mx-auto max-w-2xl text-center">
        <h2 className="text-xl font-bold text-gray-900 sm:text-2xl lg:text-3xl">
          Summary
        </h2>
      </div>

      <dl className="mt-6 sm:mt-8 lg:mt-12 grid grid-cols-2 gap-3 sm:gap-4 lg:grid-cols-4">
        <div className="flex flex-col rounded-lg border border-gray-100 px-2 py-3 sm:px-3 sm:py-4 text-center">
          <dt className="order-last text-xs sm:text-sm font-medium text-gray-500">
            Invested
          </dt>
          <dd className="text-lg sm:text-xl lg:text-2xl font-extrabold text-black-600">
            {currencyAdapter(summary.investedAmount)}
          </dd>
        </div>

        <div className="flex flex-col rounded-lg border border-gray-100 px-2 py-3 sm:px-3 sm:py-4 text-center">
          <dt className="order-last text-xs sm:text-sm font-medium text-gray-500">
            Obtained
          </dt>
          <dd className="text-lg sm:text-xl lg:text-2xl font-extrabold text-black-600">
            {currencyAdapter(summary.obtained)}
          </dd>
        </div>

        <div className="flex flex-col rounded-lg border border-gray-100 px-2 py-3 sm:px-3 sm:py-4 text-center">
          <dt className="order-last text-xs sm:text-sm font-medium text-gray-500">
            Benefit
          </dt>
          <dd className="text-lg sm:text-xl lg:text-2xl font-extrabold text-black-600">
            {currencyAdapter(summary.benefit)}
          </dd>
        </div>

        <div className="flex flex-col rounded-lg border border-gray-100 px-2 py-3 sm:px-3 sm:py-4 text-center">
          <dt className="order-last text-xs sm:text-sm font-medium text-gray-500">
            Profit
          </dt>
          <dd className="text-lg sm:text-xl lg:text-2xl font-extrabold text-black-600">
            {(100 * summary.profitability).toFixed(2)}%
          </dd>
        </div>
      </dl>
    </div>
  );
}