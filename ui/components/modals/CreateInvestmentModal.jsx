import { useState } from "react";
import { InvestmentService } from "@/lib/InvestmentService";
import LoadingSpinner from "@/components/LoadingSpinner";

export default function CreateInvestmentModal({ onClose, onCreate }) {
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [currency, setCurrency] = useState("EUR");
    const [error, setError] = useState("");
    const [isCreating, setIsCreating] = useState(false);

    const handleSubmit = async () => {
        if (!name.trim()) {
            setError("Name cannot be empty");
            return;
        }
        setError("");
        setIsCreating(true);
        try {
            const newInvestment = {
                name,
                description,
                currency,
                startDateTime: new Date().toISOString(),
                reinvested: false,
            };

            await InvestmentService.createInvestment(newInvestment);
            onCreate();
        } catch (err) {
            console.error("Failed to create investment:", err);
            setError("Failed to create investment. Please try again.");
        } finally {
            setIsCreating(false);
        }
    };

    return (
        <>
            {isCreating && (
                <div className="fixed inset-0 bg-black bg-opacity-30 flex justify-center items-center z-50">
                    <LoadingSpinner />
                </div>
            )}
            <div className="fixed inset-0 bg-black bg-opacity-30 flex justify-center items-center z-40">
                <div className="bg-white p-6 rounded shadow-lg w-full max-w-md">
                    <h2 className="text-lg font-bold mb-4">Create New Investment</h2>

                    <input
                        className="w-full border p-2 mb-3"
                        placeholder="Name"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                    />
                    <textarea
                        className="w-full border p-2 mb-3"
                        placeholder="Description"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                    />
                    <select
                        className="w-full border p-2 mb-3"
                        value={currency}
                        onChange={(e) => setCurrency(e.target.value)}
                    >
                        <option value="EUR">EUR</option>
                        <option value="USD">USD</option>
                    </select>

                    {error && <p className="text-red-500 text-sm mb-2">{error}</p>}

                    <div className="flex justify-end gap-2">
                        <button className="bg-gray-300 px-4 py-2 rounded" onClick={onClose}>
                            Cancel
                        </button>
                        <button className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600" onClick={handleSubmit}>
                            Create
                        </button>
                    </div>
                </div>
            </div>
        </>
    );
}
