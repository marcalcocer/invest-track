import { useEffect, useState } from 'react';
import { InvestmentService } from '@/lib/InvestmentService';
import LoadingSpinner from './LoadingSpinner';
import InvestmentsDetails from './investments/Details';
import InvestmentsSummary from './investments/Summary';
import { getPrivacyMode, PRIVACY_MODE_EVENT } from './PrivacyToggle';

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
      console.log('Fetching data...');
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
    return <LoadingSpinner />;
  }

  return (
    <div className="p-6 max-w-6xl mx-auto">
      <InvestmentsSummary summary={summary} isPrivate={isPrivate} />
      <InvestmentsDetails investments={investments} isPrivate={isPrivate} />
    </div>
  );
}
