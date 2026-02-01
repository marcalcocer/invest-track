import { useState } from "react";
import { InvestmentService } from "@/lib/InvestmentService";
import LoadingSpinner from "@/components/LoadingSpinner";

export default function EditInvestmentModal({ investment, onClose, onUpdate }) {
    const [name, setName] = useState(investment.name || "");
    const [description, setDescription] = useState(investment.description || "");
    const [currency, setCurrency] = useState(investment.currency || "EUR");
    const [error, setError] = useState("");
    const [isUpdating, setIsUpdating] = useState(false);

    const handleSubmit = async () => {
        if (!name.trim()) {
            setError("Name cannot be empty");
            return;
        }
        setError("");
        setIsUpdating(true);
        try {
            const updatedInvestment = {
                ...investment,
                name,
                description,
                currency,
            };
            await InvestmentService.updateInvestment(updatedInvestment.id, updatedInvestment);
            onUpdate();
        } catch (err) {
            console.error("Failed to update investment:", err);
            setError("Failed to update investment. Please try again.");
        } finally {
            setIsUpdating(false);
        }
    };

    return (
        <>
            {isUpdating && (
                <div className="fixed inset-0 bg-black bg-opacity-30 flex justify-center items-center z-50">
                    <LoadingSpinner />
                </div>
            )}
            <div className="fixed inset-0 bg-black bg-opacity-30 flex justify-center items-center z-40">
                <div className="bg-white p-6 rounded shadow-lg w-full max-w-md">
                    <h2 className="text-lg font-bold mb-4">Edit Investment</h2>
                    <div className="mb-3">
                        <label className="block text-sm font-medium mb-1">Name</label>
                        <input
                            className="w-full border p-2"
                            placeholder="Name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                        />
                    </div>
                    <div className="mb-3">
                        <label className="block text-sm font-medium mb-1">Description</label>
                        <textarea
                            className="w-full border p-2"
                            placeholder="Description"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                        />
                    </div>
                    <div className="mb-3">
                        <label className="block text-sm font-medium mb-1">Currency</label>
                        <input
                            className="w-full border p-2"
                            placeholder="Currency"
                            value={currency}
                            onChange={(e) => setCurrency(e.target.value)}
                        />
                    </div>
                    {error && <div className="text-red-500 text-sm mb-2">{error}</div>}
                    <div className="flex justify-end gap-2 mt-4">
                        <button
                            className="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300"
                            onClick={onClose}
                            disabled={isUpdating}
                        >
                            Cancel
                        </button>
                        <button
                            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                            onClick={handleSubmit}
                            disabled={isUpdating}
                        >
                            Save
                        </button>
                    </div>
                </div>
            </div>
        </>
    );
}
