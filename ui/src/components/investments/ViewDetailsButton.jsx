import { getInvestmentDetailsUrl } from "@/lib/NavigationUtils";

export default function ViewDetailsButton({ investmentId, className = "" }) {
    return (
        <div className={`flex justify-center ${className}`}>
            <button
                className="px-4 py-2 bg-blue-500 text-white text-sm font-semibold rounded-lg shadow hover:bg-blue-600 transition-colors w-full sm:w-auto"
                onClick={() => window.location.href = getInvestmentDetailsUrl(investmentId)}
            >
                View Details
            </button>
        </div>
    );
}
