import React, { useState } from "react";

export default function UpdateEntryModal({ entry, onClose, onUpdate }) {
    const [form, setForm] = useState({
        ...entry,
        id: entry.id, // Ensure id is always present in form state
        datetime: entry.datetime ? entry.datetime.slice(0, 16) : "",
        totalInvestedAmount: Math.round(entry.totalInvestedAmount),
        profitability: entry.profitability != null ? (entry.profitability * 100).toFixed(2) : ""
    });
    const [isSaving, setIsSaving] = useState(false);
    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value, id: entry.id })); // Always preserve id
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSaving(true);
        const updated = {
            ...form,
            totalInvestedAmount: Math.round(Number(form.totalInvestedAmount)),
            profitability: Number(form.profitability) / 100
        };
        await onUpdate({ ...updated, id: entry.id });
        setIsSaving(false);
    };
    return (
        <div className="fixed inset-0 bg-black bg-opacity-30 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-md">
                <h3 className="text-lg font-bold mb-4">Update Entry #{entry.id}</h3>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium">Date/Time</label>
                        <input type="datetime-local" name="datetime" value={form.datetime} onChange={handleChange} className="w-full border rounded p-2" required />
                    </div>
                    <div>
                        <label className="block text-sm font-medium">Total Invested</label>
                        <input type="number" name="totalInvestedAmount" value={form.totalInvestedAmount} onChange={handleChange} className="w-full border rounded p-2" required />
                    </div>
                    <div>
                        <label className="block text-sm font-medium">Profitability (%)</label>
                        <input type="number" step="0.01" name="profitability" value={form.profitability} onChange={handleChange} className="w-full border rounded p-2" required />
                    </div>
                    <div>
                        <label className="block text-sm font-medium">Comments</label>
                        <input type="text" name="comments" value={form.comments || ""} onChange={handleChange} className="w-full border rounded p-2" />
                    </div>
                    <div className="flex justify-end gap-2 mt-4">
                        <button type="button" className="px-4 py-2 bg-gray-300 rounded" onClick={onClose} disabled={isSaving}>Cancel</button>
                        <button type="submit" className="px-4 py-2 bg-blue-600 text-white rounded" disabled={isSaving}>{isSaving ? "Saving..." : "Update"}</button>
                    </div>
                </form>
            </div>
        </div>
    );
}
