import { useState } from "react";
import { ForecastService } from "@/lib/ForecastService";

export function useCreateForecast(investmentId, onForecastsUpdated) {
  const [isCreatingForecast, setIsCreatingForecast] = useState(false);
  const [createForecastError, setCreateForecastError] = useState(null);

  const createForecast = async (forecastData) => {
    if (!investmentId) return;
    setIsCreatingForecast(true);
    setCreateForecastError(null);
    try {
      await ForecastService.createForecast(investmentId, forecastData);
      if (onForecastsUpdated) {
        const updated = await ForecastService.fetchForecasts(investmentId);
        onForecastsUpdated(updated);
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
