import { useState } from "react";

export function useCreateForecast(investment, onForecastsUpdated) {
  const [isCreatingForecast, setIsCreatingForecast] = useState(false);
  const [createForecastError, setCreateForecastError] = useState(null);

  const createForecast = async (forecastData) => {
    if (!investment) return;
    setIsCreatingForecast(true);
    setCreateForecastError(null);
    try {
      investment.forecasts = [...(investment.forecasts || []), forecastData];
      if (onForecastsUpdated) {
        onForecastsUpdated(investment.forecasts);
      }
    } catch (err) {
      setCreateForecastError(err);
      throw err;
    } finally {
      setIsCreatingForecast(false);
    }
  };

  return { createForecast, isCreatingForecast, createForecastError };
}
