import { useState } from "react";
import { currencyAdapter } from "@/lib/currencyAdapter";
import InvestmentGraphModal from "../modals/InvestmentGraphModal";
import ConfirmDeleteModal from "../modals/ConfirmDeleteModal";
import { InvestmentService } from '@/lib/InvestmentService';
import { formatDate } from "@/lib/datetimeFormater";
import LoadingSpinner from "@/components/LoadingSpinner";
import CreateInvestmentModal from '../modals/CreateInvestmentModal';

export default function InvestmentsDetails({ investments }) {
  const [selectedInvestment, setSelectedInvestment] = useState(null);
  const [investmentToDelete, setInvestmentToDelete] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [showInactive, setShowInactive] = useState(false);
  const [showForecastModalFor, setShowForecastModalFor] = useState(null);

  const handleDelete = async (id) => {
    setIsLoading(true);
    try {
      await InvestmentService.deleteInvestment(id);
      window.location.reload();
    } catch (error) {
      console.error("Error deleting investment:", error);
      setIsLoading(false);
    }
  };

  const handleCreate = () => {
    window.location.reload();
  };

  if (isLoading) return <LoadingSpinner />;

  const handleCreateForecast = async (forecastData) => {
    // TODO: Call InvestmentService.createForecast when implemented
    setShowForecastModalFor(null);
    // Optionally reload or update state
  };

  const activeInvestments = investments.filter(i => !i.endDateTime);
  const inactiveInvestments = investments.filter(i => i.endDateTime);

  const renderInvestment = (investment) => {
    const lastEntry = investment.lastEntry;

    return (
      <li
        key={investment.id ?? `${investment.name}-${investment.currency}` }
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
            onClick={() => setSelectedInvestment(investment)}
          >
            Graph
          </button>
          <button
            className="px-3 py-2 text-xs sm:text-sm font-medium bg-gray-100 text-gray-500 border border-gray-200 rounded hover:bg-gray-200 transition duration-200"
            onClick={() => setShowForecastModalFor(investment)}
          >
            Forecast
          </button>
          <button
            className="px-3 py-2 text-xs sm:text-sm font-semibold bg-blue-600 text-white border border-blue-700 rounded hover:bg-blue-700 transition duration-200 shadow"
            onClick={() => window.location.href = `/investment?id=${investment.id}`}
          >
            Details
          </button>
          <button
            className="px-3 py-2 text-xs sm:text-sm font-semibold bg-red-500 text-white border border-red-700 rounded hover:bg-red-600 transition duration-200 shadow"
            onClick={() => setInvestmentToDelete(investment)}
          >
            Delete
          </button>
        </div>
      </li>
    );
  };

  return (
    <div className="px-3 sm:px-4 lg:px-6 py-8 sm:py-12">
      {/* Main title adjusted */}
      <h2 className="text-xl font-bold text-gray-900 sm:text-2xl lg:text-3xl text-center">
        Investments Details
      </h2>

      <ul className="space-y-6 sm:space-y-8 mt-8 sm:mt-12">
        {activeInvestments.map(renderInvestment)}
      </ul>

      {/* Create Button - more compact on mobile */}
      <div className="mt-6 sm:mt-8 flex justify-center">
        <button
          onClick={() => setIsModalOpen(true)}
          className="px-3 py-2 sm:px-4 sm:py-2 text-xs sm:text-sm font-medium bg-green-500 text-white rounded-md shadow hover:bg-green-600 transition duration-200"
        >
          Create Investment
        </button>
      </div>

      {/* Toggle Inactive Investments - more compact */}
      <div className="my-8 sm:my-12 text-center">
        <button
          className="px-3 py-2 text-xs sm:text-sm font-medium bg-gray-200 text-gray-700 border border-gray-400 rounded hover:bg-gray-300 transition duration-200"
          onClick={() => setShowInactive(!showInactive)}
        >
          {showInactive ? 'Hide Inactive' : 'Show Inactive'}
        </button>
      </div>

      {showInactive && (
        <div>
          {/* Inactive investments title adjusted */}
          <h3 className="text-lg sm:text-xl font-semibold text-gray-800 text-center mb-3 sm:mb-4">
            Inactive Investments
          </h3>
          <ul className="space-y-6 sm:space-y-8">
            {inactiveInvestments.map(renderInvestment)}
          </ul>
        </div>
      )}

      {isModalOpen && (
        <CreateInvestmentModal
          onClose={() => setIsModalOpen(false)}
          onCreate={handleCreate}
        />
      )}

      {selectedInvestment && (
        <InvestmentGraphModal 
          investment={selectedInvestment} 
          onClose={() => setSelectedInvestment(null)} 
          showDetailsButton={true} 
        />
      )}

      {investmentToDelete && (
        <ConfirmDeleteModal
          entity="investment"
          id={investmentToDelete.id}
          name={investmentToDelete.name}
          onCancel={() => setInvestmentToDelete(null)}
          onConfirm={handleDelete}
        />
      )}
    </div>
  );
}