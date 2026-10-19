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
                <div className="fixed inset-0 bg-black/60 flex justify-center items-center z-50">
                    <LoadingSpinner />
                </div>
            )}
            <div className="fixed inset-0 bg-black/60 flex justify-center items-center z-40">
                <div className="bg-card text-card-foreground border border-border p-6 rounded shadow-lg w-full max-w-md">
                    <h2 className="text-lg font-bold mb-4">Create New Investment</h2>

                    <input
                        className="w-full border border-border bg-background text-foreground placeholder:text-muted-foreground rounded p-2 mb-3 focus:outline-none focus:ring-2 focus:ring-ring"
                        placeholder="Name"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                    />
                    <textarea
                        className="w-full border border-border bg-background text-foreground placeholder:text-muted-foreground rounded p-2 mb-3 focus:outline-none focus:ring-2 focus:ring-ring"
                        placeholder="Description"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                    />
                    <select
                        className="w-full border border-border bg-background text-foreground placeholder:text-muted-foreground rounded p-2 mb-3 focus:outline-none focus:ring-2 focus:ring-ring"
                        value={currency}
                        onChange={(e) => setCurrency(e.target.value)}
                    >
                        <option value="EUR">EUR</option>
                        <option value="USD">USD</option>
                    </select>

                    {error && <p className="text-danger text-sm mb-2">{error}</p>}

                    <div className="flex justify-end gap-2">
                        <button className="bg-muted text-foreground border border-border px-4 py-2 rounded" onClick={onClose}>
                            Cancel
                        </button>
                        <button className="bg-primary text-primary-foreground px-4 py-2 rounded hover:opacity-90" onClick={handleSubmit}>
                            Create
                        </button>
                    </div>
                </div>
            </div>
        </>
    );
}
