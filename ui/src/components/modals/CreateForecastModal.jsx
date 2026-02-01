import { useState } from "react";
import LoadingSpinner from "@/components/LoadingSpinner";
import { ForecastService } from "@/lib/ForecastService";

export default function CreateForecastModal({ investment, entries, onClose, onCreate }) {
    const [name, setName] = useState("");
    const [startEntryId, setStartEntryId] = useState(entries && entries.length > 0 ? String(entries[0].id) : "");
    const [months, setMonths] = useState(12);
    const [pessimistRate, setPessimistRate] = useState(0);
    const [neutralRate, setNeutralRate] = useState(0);
    const [optimistRate, setOptimistRate] = useState(0);
    const [isCreating, setIsCreating] = useState(false);
    const [error, setError] = useState("");

    const handleSubmit = async () => {
        if (!name.trim()) {
            setError("Name cannot be empty");
            return;
        }
        if (!startEntryId) {
            setError("Start entry must be selected");
            return;
        }
        if (Number(months) < 1) {
            setError("Duration (months) must be at least 1");
            return;
        }
        setIsCreating(true);
        setError("");
        try {
            // Find the selected start entry
            const startEntry = entries.find(e => String(e.id) === String(startEntryId));
            let startDate = startEntry ? startEntry.datetime : null;
            let endDate = null;
            if (startDate) {
                // Parse as ISO string and add months
                const d = new Date(startDate);
                d.setMonth(d.getMonth() + Number(months));
                endDate = d.toISOString().split('T')[0];
                startDate = new Date(startDate).toISOString().split('T')[0];
            }
            if (onCreate) onCreate({
                name,
                startEntryId: Number(startEntryId),
                months: Number(months),
                startDate,
                endDate,
                scenarioRates: {
                    PESSIMIST: Number(pessimistRate),
                    NEUTRAL: Number(neutralRate),
                    OPTIMIST: Number(optimistRate)
                }
            });
        } catch (err) {
            setError("Failed to create forecast. Please try again.");
        } finally {
            setIsCreating(false);
        }
    };

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
            <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-md">
                <h2 className="text-xl font-bold mb-4">Create Forecast</h2>
                {error && <div className="text-red-600 mb-2">{error}</div>}
                <label className="block mb-2">Forecast Name
                    <input className="w-full border rounded p-2 mt-1" value={name} onChange={e => setName(e.target.value)} />
                </label>
                <label className="block mb-2">Start Entry
                    <select className="w-full border rounded p-2 mt-1" value={startEntryId} onChange={e => setStartEntryId(e.target.value)}>
                        {entries.map(entry => (
                            <option key={entry.id} value={String(entry.id)}>
                                {entry.id} - {entry.datetime}
                            </option>
                        ))}
                    </select>
                </label>
                <label className="block mb-2">Duration (months)
                    <input type="number" className="w-full border rounded p-2 mt-1" value={months} min={1} onChange={e => setMonths(e.target.value)} />
                </label>
                <div className="mb-2">
                    <label className="block">Pessimist Monthly Growth Rate (%)
                        <input type="number" className="w-full border rounded p-2 mt-1" value={pessimistRate} onChange={e => setPessimistRate(e.target.value)} />
                    </label>
                </div>
                <div className="mb-2">
                    <label className="block">Neutral Monthly Growth Rate (%)
                        <input type="number" className="w-full border rounded p-2 mt-1" value={neutralRate} onChange={e => setNeutralRate(e.target.value)} />
                    </label>
                </div>
                <div className="mb-4">
                    <label className="block">Optimist Monthly Growth Rate (%)
                        <input type="number" className="w-full border rounded p-2 mt-1" value={optimistRate} onChange={e => setOptimistRate(e.target.value)} />
                    </label>
                </div>
                <div className="flex justify-end gap-2">
                    <button className="px-4 py-2 rounded bg-gray-200" onClick={onClose} disabled={isCreating}>Cancel</button>
                    <button className="px-4 py-2 rounded bg-blue-600 text-white" onClick={handleSubmit} disabled={isCreating}>
                        {isCreating ? <LoadingSpinner size={4} /> : "Create"}
                    </button>
                </div>
            </div>
        </div>
    );
}
