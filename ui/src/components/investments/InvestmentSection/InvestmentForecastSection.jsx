import LoadingSpinner from "@/components/LoadingSpinner";
import ConfirmDeleteModal from "@/components/modals/ConfirmDeleteModal";

export default function InvestmentForecastSection({
    forecasts,
    isLoadingForecasts,
    onDeleteForecast,
    isConfirmingDeleteForecast,
    setIsConfirmingDeleteForecast,
    onEditForecast,
    onViewGraph
}) {
    return (
        <div className="mt-8">
            <h3 className="text-md sm:text-lg font-semibold mb-2 text-center">Forecasts</h3>
            {isLoadingForecasts ? (
                <div className="py-4"><LoadingSpinner /></div>
            ) : forecasts.length === 0 ? (
                <div className="flex justify-center items-center py-8">
                    <span className="text-gray-500 text-center">No forecasts available</span>
                </div>
            ) : (
                <div className="mt-4">
                    <table className="mx-auto border-collapse border border-gray-200 text-xs sm:text-sm">
                        <thead>
                            <tr className="bg-gray-100">
                                <th className="border p-2 whitespace-nowrap">Name</th>
                                <th className="border p-2 whitespace-nowrap">Start Date</th>
                                <th className="border p-2 whitespace-nowrap">Months</th>
                                <th className="border p-2 whitespace-nowrap">Pessimist %</th>
                                <th className="border p-2 whitespace-nowrap">Neutral %</th>
                                <th className="border p-2 whitespace-nowrap">Optimist %</th>
                                <th className="border p-2 whitespace-nowrap">Actions</th>
                                <th className="border p-2 whitespace-nowrap">Graph</th>
                            </tr>
                        </thead>
                        <tbody>
                            {forecasts.map((f) => (
                                <tr key={f.id} className="border">
                                    <td className="border p-2 text-center whitespace-nowrap">{f.name}</td>
                                    <td className="border p-2 text-center whitespace-nowrap">{
                                        (() => {
                                            if (f.startEntryId && Array.isArray(f.entriesFromParent)) {
                                                const entry = f.entriesFromParent.find(e => String(e.id) === String(f.startEntryId));
                                                return entry ? new Date(entry.datetime).toISOString().split('T')[0] : f.startDate ?? '';
                                            }
                                            return f.startDate ?? '';
                                        })()
                                    }</td>
                                    <td className="border p-2 text-center whitespace-nowrap">{f.startDate && f.endDate ? Math.max(1, Math.round((new Date(f.endDate).getFullYear() * 12 + new Date(f.endDate).getMonth()) - (new Date(f.startDate).getFullYear() * 12 + new Date(f.startDate).getMonth()))) : ''}</td>
                                    <td className="border p-2 text-center whitespace-nowrap">{f.scenarioRates?.PESSIMIST ?? 0}%</td>
                                    <td className="border p-2 text-center whitespace-nowrap">{f.scenarioRates?.NEUTRAL ?? 0}%</td>
                                    <td className="border p-2 text-center whitespace-nowrap">{f.scenarioRates?.OPTIMIST ?? 0}%</td>
                                    <td className="border p-2 text-center whitespace-nowrap">
                                        <div className="flex flex-row items-center justify-center gap-2">
                                            <button
                                                className="px-2 py-1 bg-blue-500 text-white text-xs rounded hover:bg-blue-600 transition duration-200"
                                                onClick={() => onEditForecast(f)}
                                            >
                                                Edit
                                            </button>
                                            <button
                                                className="px-2 py-1 bg-red-500 text-white text-xs rounded hover:bg-red-600 transition duration-200"
                                                onClick={() => setIsConfirmingDeleteForecast(f.id)}
                                            >
                                                Delete
                                            </button>
                                        </div>
                                    </td>
                                    <td className="border p-2 text-center whitespace-nowrap">
                                        <button
                                            className="px-2 py-1 bg-purple-500 text-white text-xs rounded hover:bg-purple-600 transition duration-200"
                                            onClick={() => onViewGraph(f)}
                                        >
                                            View Graph
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
            {isConfirmingDeleteForecast && (
                <ConfirmDeleteModal
                    entity="forecast"
                    id={isConfirmingDeleteForecast}
                    name={forecasts.find(f => f.id === isConfirmingDeleteForecast)?.name || ''}
                    onCancel={() => setIsConfirmingDeleteForecast(null)}
                    onConfirm={onDeleteForecast}
                />
            )}
        </div>
    );
}
