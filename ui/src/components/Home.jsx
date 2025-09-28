import { useEffect, useState } from 'react';
import { InvestmentService } from '@/lib/InvestmentService';
import LoadingSpinner from './LoadingSpinner';
import InvestmentsDetails from './investments/Details';
import InvestmentsSummary from './investments/Summary';

export default function Home() {
  const [isLoading, setIsLoading] = useState(true);
  const [investments, setInvestments] = useState([]);
  const [summary, setSummary] = useState({});

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
      <InvestmentsSummary summary={summary} />
      <InvestmentsDetails investments={investments} />
    </div>
  );
}
