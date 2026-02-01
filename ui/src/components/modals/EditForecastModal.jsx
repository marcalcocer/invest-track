import { useState } from "react";
import LoadingSpinner from "@/components/LoadingSpinner";
import { ForecastService } from "@/lib/ForecastService";

export default function EditForecastModal({ forecast, entries, onClose, onUpdate }) {
    const [name, setName] = useState(forecast.name || "");
    const [startEntryId, setStartEntryId] = useState(forecast.startEntryId ? String(forecast.startEntryId) : (entries && entries.length > 0 ? String(entries[0].id) : ""));
    const [months, setMonths] = useState(forecast.months || 12);
    const [pessimistRate, setPessimistRate] = useState(forecast.scenarioRates?.PESSIMIST ?? 0);
    const [neutralRate, setNeutralRate] = useState(forecast.scenarioRates?.NEUTRAL ?? 0);
    const [optimistRate, setOptimistRate] = useState(forecast.scenarioRates?.OPTIMIST ?? 0);
    const [isUpdating, setIsUpdating] = useState(false);
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
        setIsUpdating(true);
        setError("");
        try {
            const startEntry = entries.find(e => String(e.id) === String(startEntryId));
            let startDate = startEntry ? startEntry.datetime : null;
            let endDate = null;
            if (startDate) {
                const d = new Date(startDate);
                d.setMonth(d.getMonth() + Number(months));
                endDate = d.toISOString().split('T')[0];
                startDate = new Date(startDate).toISOString().split('T')[0];
            }
            const updatedForecast = {
                ...forecast,
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
            };
            await ForecastService.updateForecast(forecast.id, updatedForecast);
            if (onUpdate) onUpdate();
        } catch (err) {
            setError("Failed to update forecast. Please try again.");
        } finally {
            setIsUpdating(false);
        }
    };

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
            <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-md">
                <h2 className="text-xl font-bold mb-4">Edit Forecast</h2>
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
                    <button className="px-4 py-2 rounded bg-gray-200" onClick={onClose} disabled={isUpdating}>Cancel</button>
                    <button className="px-4 py-2 rounded bg-blue-600 text-white" onClick={handleSubmit} disabled={isUpdating}>
                        {isUpdating ? <LoadingSpinner size={4} /> : "Update"}
                    </button>
                </div>
            </div>
        </div>
    );
}
