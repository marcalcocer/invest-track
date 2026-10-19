import { useEffect, useState } from 'react';
import { InvestmentService } from '@/lib/InvestmentService';
import Skeleton from './ui/Skeleton';
import AIMessage from './AIMessage';
import InvestmentsDetails from './investments/Details';
import InvestmentsSummary from './investments/Summary';
import { getPrivacyMode, PRIVACY_MODE_EVENT } from './PrivacyToggle';

function HomeSkeleton() {
  return (
    <div className="px-4 py-6 sm:px-6 max-w-6xl mx-auto" role="status" aria-label="Loading your investments">
      <Skeleton className="h-7 w-40 mb-5" />
      <div className="grid grid-cols-1 min-[480px]:grid-cols-2 gap-3 sm:gap-4 lg:grid-cols-4 mb-8">
        {[0, 1, 2, 3].map((key) => (
          <Skeleton key={key} className="h-28 rounded-token" />
        ))}
      </div>
      <Skeleton className="h-64 rounded-token" />
    </div>
  );
}

export default function Home() {
  const [isLoading, setIsLoading] = useState(true);
  const [investments, setInvestments] = useState([]);
  const [summary, setSummary] = useState({});
  const [isPrivate, setIsPrivate] = useState(false);

  useEffect(() => {
    setIsPrivate(getPrivacyMode());
    
    const handlePrivacyChange = (e) => {
      setIsPrivate(e.detail);
    };

    window.addEventListener(PRIVACY_MODE_EVENT, handlePrivacyChange);
    return () => window.removeEventListener(PRIVACY_MODE_EVENT, handlePrivacyChange);
  }, []);

  useEffect(() => {
    async function fetchData() {
      try {
        const [investData, summaryData] = await Promise.all([
          InvestmentService.fetchInvestments(),
          InvestmentService.fetchSummary(),
        ]);

        setInvestments(investData);
        setSummary({
          investedAmount: summaryData.investedAmount,
          obtained: summaryData.obtained,
          benefit: summaryData.benefit,
          profitability: summaryData.profitability,
          initialInvestedAmount: summaryData.initialInvestedAmount,
          initialObtained: summaryData.initialObtained,
          initialBenefit: summaryData.initialBenefit,
          initialProfitability: summaryData.initialProfitability,
        });
      } catch (error) {
        console.error('Error fetching data', error);
      } finally {
        setIsLoading(false);
      }
    }

    fetchData();
  }, []);

  if (isLoading) {
    return <HomeSkeleton />;
  }

  return (
    <div className="px-4 py-6 sm:px-6 max-w-6xl mx-auto">
      <AIMessage />
      <InvestmentsSummary summary={summary} isPrivate={isPrivate} />
      {/* Details still use light-only chart styling until their own phase. */}
      <div className="rounded-token border border-border bg-card text-card-foreground shadow-sm">
        <InvestmentsDetails investments={investments} isPrivate={isPrivate} />
      </div>
    </div>
  );
}
