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

  const activeInvestments = investments.filter(i => !i.endDateTime);
  const inactiveInvestments = investments.filter(i => i.endDateTime);

  const renderInvestment = (investment) => {
    const lastEntry = investment.lastEntry;

    return (
      <li
        key={investment.id}
        className="bg-white shadow-md rounded-lg p-3 sm:p-4 border border-gray-200 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3 sm:gap-4 lg:gap-6 items-center"
      >
        {/* Name & Description */}
        <div className="text-center sm:text-left">
          <h3 className="text-base sm:text-lg font-semibold text-gray-700">{investment.name}</h3>
          <p className="text-gray-600 text-xs sm:text-sm">{investment.description} ({investment.currency})</p>
          <p className="text-gray-500 text-xs mt-1">
            {formatDate(investment.startDateTime)} - {investment.endDateTime ? formatDate(investment.endDateTime) : 'Present'}
          </p>
        </div>

        {/* Profitability & Obtained & Benefit */}
        <div className="text-center sm:text-right">
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

        {/* Graph Button */}
        <button
          className="w-full lg:w-auto px-3 py-2 bg-blue-500 text-white text-xs sm:text-sm font-semibold rounded-lg shadow hover:bg-blue-600 transition duration-200"
          onClick={() => setSelectedInvestment(investment)}
        >
          Graph
        </button>

        {/* Details Button */}
        <button
          className="w-full lg:w-auto px-3 py-2 bg-green-500 text-white text-xs sm:text-sm font-semibold rounded-lg shadow hover:bg-green-600 transition duration-200"
          onClick={() => window.location.href = `/investment?id=${investment.id}`}
        >
          Details
        </button>

        {/* Delete Button */}
        <button
          className="w-full lg:w-auto px-3 py-2 bg-red-500 text-white text-xs sm:text-sm font-semibold rounded-lg shadow hover:bg-red-600 transition duration-200"
          onClick={() => setInvestmentToDelete(investment)}
        >
          Delete
        </button>
      </li>
    );
  };

  return (
    <div className="px-3 sm:px-4 lg:px-6 py-4 sm:py-6">
      {/* Main title adjusted */}
      <h2 className="text-xl font-bold text-gray-900 sm:text-2xl lg:text-3xl text-center">
        Investments Details
      </h2>

      <ul className="space-y-3 sm:space-y-4 mt-4 sm:mt-6">
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
      <div className="my-4 sm:my-6 text-center">
        <button
          className="px-3 py-2 text-xs sm:text-sm bg-gray-200 text-gray-700 rounded hover:bg-gray-300 transition duration-200"
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
          <ul className="space-y-3 sm:space-y-4">
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