import LoadingSpinner from "@/components/LoadingSpinner";
import ConfirmDeleteModal from "@/components/modals/ConfirmDeleteModal";
import { formatValueForPrivacy } from "@/lib/currencyAdapter";
import { ForecastUtils } from "@/lib/ForecastUtils";

export default function InvestmentForecastSection({
    forecasts,
    investment,
    isLoadingForecasts,
    onDeleteForecast,
    isConfirmingDeleteForecast,
    setIsConfirmingDeleteForecast,
    onEditForecast,
    onViewGraph,
    isPrivate
}) {
    return (
        <div className="mt-8">
            <h3 className="text-md sm:text-lg font-semibold mb-2 text-center">Forecasts</h3>
            {isLoadingForecasts ? (
                <div className="py-4"><LoadingSpinner /></div>
            ) : forecasts.length === 0 ? (
                <div className="flex justify-center items-center py-8">
                    <span className="text-muted-foreground text-center">No forecasts available</span>
                </div>
            ) : (
                <div className="mt-4 overflow-x-auto">
                    <table className="mx-auto border-collapse border border-border text-foreground text-xs sm:text-sm">
                        <thead>
                            <tr className="bg-muted text-foreground">
                                <th className="border border-border p-2 whitespace-nowrap">Name</th>
                                <th className="border border-border p-2 whitespace-nowrap">Start Date</th>
                                <th className="border border-border p-2 whitespace-nowrap">Months</th>
                                <th className="border border-border p-2 whitespace-nowrap">Pessimist %/mo</th>
                                <th className="border border-border p-2 whitespace-nowrap">Neutral %/mo</th>
                                <th className="border border-border p-2 whitespace-nowrap">Optimist %/mo</th>
                                <th className="border border-border p-2 whitespace-nowrap">Contribution /mo</th>
                                <th className="border border-border p-2 whitespace-nowrap">Actions</th>
                                <th className="border border-border p-2 whitespace-nowrap">Graph</th>
                            </tr>
                        </thead>
                        <tbody>
                            {forecasts.map((f) => (
                                <tr key={f.id} className="border border-border bg-card">
                                    <td className="border border-border p-2 text-center whitespace-nowrap">{f.name}</td>
                                    <td className="border border-border p-2 text-center whitespace-nowrap">{
                                        (() => {
                                            if (f.startEntryId && Array.isArray(f.entriesFromParent)) {
                                                const entry = f.entriesFromParent.find(e => String(e.id) === String(f.startEntryId));
                                                return entry ? new Date(entry.datetime).toISOString().split('T')[0] : f.startDate ?? '';
                                            }
                                            return f.startDate ?? '';
                                        })()
                                    }</td>
                                    <td className="border border-border p-2 text-center whitespace-nowrap">{f.startDate && f.endDate ? ForecastUtils.monthsBetween(f.startDate, f.endDate) : ''}</td>
                                    <td className="border border-border p-2 text-center whitespace-nowrap">{f.scenarioRates?.PESSIMIST ?? 0}%</td>
                                    <td className="border border-border p-2 text-center whitespace-nowrap">{f.scenarioRates?.NEUTRAL ?? 0}%</td>
                                    <td className="border border-border p-2 text-center whitespace-nowrap">{f.scenarioRates?.OPTIMIST ?? 0}%</td>
                                    <td className="border border-border p-2 text-center whitespace-nowrap">{formatValueForPrivacy(f.monthlyContribution ?? 0, investment?.currency, isPrivate)}</td>
                                    <td className="border border-border p-2 text-center whitespace-nowrap">
                                        <div className="flex flex-row items-center justify-center gap-2">
                                            <button
                                                className="px-2 py-1 bg-primary text-primary-foreground text-xs rounded hover:opacity-90 transition duration-200"
                                                onClick={() => onEditForecast(f)}
                                            >
                                                Edit
                                            </button>
                                            <button
                                                className="px-2 py-1 bg-danger text-white text-xs rounded hover:opacity-90 transition duration-200"
                                                onClick={() => setIsConfirmingDeleteForecast(f.id)}
                                            >
                                                Delete
                                            </button>
                                        </div>
                                    </td>
                                    <td className="border border-border p-2 text-center whitespace-nowrap">
                                        <button
                                            className="px-2 py-1 bg-primary/80 text-primary-foreground text-xs rounded hover:bg-primary transition duration-200"
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
