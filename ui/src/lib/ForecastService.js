import { API_BASE_URL } from "./config";

const FORECASTS_PATH = '/investments/forecast';

export const ForecastService = {
  async fetchForecasts(investmentId) {
    const res = await fetch(`${API_BASE_URL}${FORECASTS_PATH}/${investmentId}`);
    if (!res.ok) {
        if (res.status === 204) return [];
        throw new Error("Failed to fetch forecasts");
    }
    return res.json();
  },
  async createForecast(investmentId, forecast) {
    const res = await fetch(`${API_BASE_URL}${FORECASTS_PATH}/${investmentId}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(forecast),
    });
    if (!res.ok) throw new Error("Failed to create forecast");
    return res.json();
  },
  async updateForecast(investmentId, forecastId, forecast) {
    const res = await fetch(`${API_BASE_URL}${FORECASTS_PATH}/${investmentId}/${forecastId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(forecast),
    });
    if (!res.ok) throw new Error("Failed to update forecast");
    return res.json();
  },
  async deleteForecast(investmentId, forecastId) {
    const res = await fetch(`${API_BASE_URL}${FORECASTS_PATH}/${investmentId}/${forecastId}`, {
      method: "DELETE" });
    if (!res.ok) throw new Error("Failed to delete forecast");
    return res.json();
  },
};
