import { useState, useEffect } from "react";
import InvestmentGraphModal from "../modals/InvestmentGraphModal";
import CombinedForecastGraphModal from "../modals/CombinedForecastGraphModal";
import ConfirmDeleteModal from "../modals/ConfirmDeleteModal";
import { InvestmentService } from '@/lib/InvestmentService';
import LoadingSpinner from "@/components/LoadingSpinner";
import CreateInvestmentModal from '../modals/CreateInvestmentModal';
import EditInvestmentModal from '../modals/EditInvestmentModal';
import InvestmentsDetailsListItem from "./InvestmentsDetailsSection/InvestmentsDetailsListItem";
import { ForecastService } from '@/lib/ForecastService';

export default function InvestmentsDetails({ investments }) {
  const [investmentsWithForecasts, setInvestmentsWithForecasts] = useState([]);

  useEffect(() => {
    async function fetchAllForecasts() {
      const results = await Promise.all(
        investments.map(async (inv) => {
          try {
            const forecasts = await ForecastService.fetchForecasts(inv.id);
            return { ...inv, forecasts };
          } catch {
            return { ...inv, forecasts: [] };
          }
        })
      );
      setInvestmentsWithForecasts(results);
    }
    fetchAllForecasts();
  }, [investments]);

  const [selectedInvestment, setSelectedInvestment] = useState(null);
  const [investmentToDelete, setInvestmentToDelete] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [showInactive, setShowInactive] = useState(false);
  const [showForecastModalFor, setShowForecastModalFor] = useState(null);
  const [editingInvestment, setEditingInvestment] = useState(null);

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

  if (isLoading || investmentsWithForecasts.length === 0) return <LoadingSpinner />;

  const handleCreateForecast = () => {
    setShowForecastModalFor(null);
  };

  const activeInvestments = investmentsWithForecasts.filter(i => !i.endDateTime);
  const inactiveInvestments = investmentsWithForecasts.filter(i => i.endDateTime);

  const renderInvestment = (investment) => (
    <InvestmentsDetailsListItem
      key={investment.id ?? `${investment.name}-${investment.currency}`}
      investment={investment}
      onGraph={() => setSelectedInvestment(investment)}
      onForecast={() => setShowForecastModalFor(investment)}
      onDetails={() => window.location.href = `/investment?id=${investment.id}`}
      onDelete={() => setInvestmentToDelete(investment)}
      onEdit={() => setEditingInvestment(investment)}
    />
  );

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

      {editingInvestment && (
        <EditInvestmentModal
          investment={editingInvestment}
          onClose={() => setEditingInvestment(null)}
          onUpdate={() => window.location.reload()}
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

      {selectedInvestment && (
        <InvestmentGraphModal
          investment={selectedInvestment}
          onClose={() => setSelectedInvestment(null)}
          showDetailsButton={true}
        />
      )}
      {showForecastModalFor && (
        <CombinedForecastGraphModal
          investment={showForecastModalFor}
          forecasts={showForecastModalFor.forecasts || []}
          onClose={() => setShowForecastModalFor(null)}
        />
      )}
    </div>
  );
}