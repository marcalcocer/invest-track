import { useState, useEffect } from "react";
import LoadingSpinner from "@/components/LoadingSpinner";
import { InvestmentService } from "@/lib/InvestmentService";
import { currencyAdapter } from "@/lib/currencyAdapter";

export default function CreateEntryModal({ investment, onClose, onCreate }) {
    const lastEntry = investment && investment.lastEntry ? investment.lastEntry : null;

    const [inputType, setInputType] = useState("manual"); // "manual" or "total"

    const [datetime, setDatetime] = useState("");
    const [useNow, setUseNow] = useState(true);
    const [comments, setComments] = useState("");
    const [obtained, setObtained] = useState(0);
    const [benefit, setBenefit] = useState(0);
    const [isLoading, setIsLoading] = useState(false);

    const [initialInvestedAmount, setInitialInvestedAmount] = useState(lastEntry ? lastEntry.initialInvestedAmount : 0);
    const [reinvestedAmount, setReinvestedAmount] = useState(lastEntry ? lastEntry.reinvestedAmount : 0);
    const [totalInvestedAmount, setTotalInvestedAmount] = useState(lastEntry ? lastEntry.totalInvestedAmount : 0);
    const [profitability, setProfitability] = useState(lastEntry ? (lastEntry.profitability * 100).toFixed(2) : 0);
    // Recalculate values whenever inputs change
    useEffect(() => {
        let initial = 0;
        const reinvested = parseFloat(reinvestedAmount);
        let totalInvested = 0;

        if (inputType === "manual") {
            initial = parseFloat(initialInvestedAmount);

            totalInvested = initial + reinvested;
            setTotalInvestedAmount(totalInvested);
        } else if (inputType === "total") {
            totalInvested = parseFloat(totalInvestedAmount);

            initial = totalInvested - reinvested;
            setInitialInvestedAmount(initial);
        } else {
            console.error("Invalid input type");
        }

        const profit = parseFloat(profitability);
        const obtainedValue = totalInvested * (1 + profit / 100);
        const benefitValue = obtainedValue - totalInvested;

        setObtained(obtainedValue);
        setBenefit(benefitValue);
    }, [initialInvestedAmount, reinvestedAmount, profitability, totalInvestedAmount, inputType]);

    const handleCreate = async () => {
        setIsLoading(true);
        try {
            // Use current datetime if "Use Now" is checked, otherwise use the provided datetime.
            const finalDatetime = useNow
                ? new Date().toISOString()
                : new Date(datetime).toISOString();

            const newEntry = {
                datetime: finalDatetime,
                comments,
                initialInvestedAmount: parseFloat(initialInvestedAmount),
                reinvestedAmount: parseFloat(reinvestedAmount),
                profitability: parseFloat((profitability / 100).toFixed(4)),
                totalInvestedAmount: parseFloat(totalInvestedAmount),
                obtained,
                benefit,
            };

            await InvestmentService.createInvestmentEntry(investment.id, newEntry);
            onCreate();
            setIsLoading(false);
            onClose();
        } catch (error) {
            console.error("Error creating entry", error);
        } finally {
            setIsLoading(false);
        }
    };

    if (isLoading) {
        return <LoadingSpinner />;
    }

    return (
        <div className="fixed inset-0 bg-black/60 flex justify-center items-center">
            <div className="bg-card text-card-foreground border border-border p-6 rounded-lg shadow-lg w-full max-w-md mx-2">
                <h2 className="text-xl font-semibold mb-4">Create New Entry</h2>

                {/* Datetime*/}
                <label className="block text-foreground">Datetime:</label>
                <div className="flex items-center mb-2">
                    <input
                        type="checkbox"
                        checked={useNow}
                        onChange={(e) => setUseNow(e.target.checked)}
                        className="mr-2"
                    />
                    <span className="text-foreground">Use current time (Now)</span>
                </div>
                {!useNow && (
                    <input
                        type="datetime-local"
                        className="w-full border border-border bg-background text-foreground p-2 rounded mb-4 focus:outline-none focus:ring-2 focus:ring-ring"
                        value={datetime}
                        onChange={(e) => setDatetime(e.target.value)}
                    />
                )}

                <hr className="my-4 border-border" />

                {/* Chooser for input type */}
                <div className="mb-4">
                    <span className="text-foreground">Input type:</span>
                    <div className="mt-2 grid grid-cols-1 gap-2">
                        <label className="mr-4">
                            <input
                                type="radio"
                                name="inputType"
                                value="manual"
                                checked={inputType === "manual"}
                                onChange={(e) => setInputType(e.target.value)}
                                className="mr-2"
                            />
                            Manual input amounts
                        </label>
                        <label>
                            <input
                                type="radio"
                                name="inputType"
                                value="total"
                                checked={inputType === "total"}
                                onChange={(e) => setInputType(e.target.value)}
                                className="mr-2"
                            />
                            Total investment amount input
                        </label>
                    </div>
                </div>

                <hr className="my-4 border-border" />

                {inputType === "manual" ? (
                    <>
                        <label className="block text-foreground">Initial Invested Amount</label>
                        <input
                            type="number"
                            step="0.01"
                            className="w-full border border-border bg-background text-foreground p-2 rounded mb-4 focus:outline-none focus:ring-2 focus:ring-ring"
                            value={initialInvestedAmount}
                            onChange={(e) => setInitialInvestedAmount(e.target.value)}
                        />

                        <label className="block text-foreground">Reinvested Amount</label>
                        <input
                            type="number"
                            step="0.01"
                            className="w-full border border-border bg-background text-foreground p-2 rounded mb-4 focus:outline-none focus:ring-2 focus:ring-ring"
                            value={reinvestedAmount}
                            onChange={(e) => setReinvestedAmount(e.target.value)}
                        />
                    </>
                ) : (
                    <>
                        <label className="block text-foreground">Total Invested Amount</label>
                        <input
                            type="number"
                            step="0.01"
                            className="w-full border border-border bg-background text-foreground p-2 rounded mb-4 focus:outline-none focus:ring-2 focus:ring-ring"
                            value={totalInvestedAmount}
                            onChange={(e) => setTotalInvestedAmount(e.target.value)}
                        />
                    </>
                )}

                <label className="block text-foreground">Profitability (%)</label>
                <input
                    type="number"
                    step="0.01"
                    className="w-full border border-border bg-background text-foreground p-2 rounded mb-4 focus:outline-none focus:ring-2 focus:ring-ring"
                    value={profitability}
                    onChange={(e) => setProfitability(e.target.value)}
                />

                <label className="block text-foreground">Comments:</label>
                <input
                    type="text"
                    className="w-full border border-border bg-background text-foreground p-2 rounded mb-4 focus:outline-none focus:ring-2 focus:ring-ring"
                    value={comments}
                    onChange={(e) => setComments(e.target.value)}
                />

                <div className="bg-muted rounded-lg p-3 mb-4">
                    {inputType === "total" && (
                        <span>
                            <p className="text-foreground"><strong>Initial Invested Amount:</strong> {currencyAdapter(initialInvestedAmount, investment.currency)}</p>
                            <p className="text-foreground"><strong>Reinvested Amount:</strong> {currencyAdapter(reinvestedAmount, investment.currency)}</p>
                        </span>
                    )}
                    <p className="text-foreground"><strong>Total Invested Amount:</strong> {currencyAdapter(totalInvestedAmount, investment.currency)}</p>
                    <p className="text-foreground"><strong>Obtained:</strong> {currencyAdapter(obtained, investment.currency)}</p>
                    <p className="text-foreground"><strong>Benefit:</strong> {currencyAdapter(benefit, investment.currency)}</p>
                </div>

                <div className="flex justify-end space-x-2 ">
                    <button
                        className="px-4 py-2 bg-muted text-foreground border border-border rounded"
                        onClick={onClose}
                    >
                        Cancel
                    </button>
                    <button
                        className="px-4 py-2 bg-primary text-primary-foreground rounded"
                        onClick={handleCreate}
                    >
                        Create
                    </button>
                </div>
            </div>
        </div>
    );
}
