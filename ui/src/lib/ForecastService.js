import { API_BASE_URL } from "./config";

const FORECASTS_PATH = '/investments/forecast';

export const ForecastService = {
  async createForecast(investmentId, forecast) {
    const res = console.log('ForecastService.createForecast called', investmentId, forecast);
      await fetch(`${API_BASE_URL}${FORECASTS_PATH}/${investmentId}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ ...forecast, investmentId }),
    });
    if (!res.ok) throw new Error("Failed to create forecast, res status: " + res.status);
    return res.json();
  },
  async updateForecast(investmentId, forecastId, forecast) {
    const res = await fetch(`${API_BASE_URL}${FORECASTS_PATH}/${investmentId}/${forecastId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(forecast),
    });
    if (!res.ok) throw new Error("Failed to update forecast, res status: " + res.status);
    return res.json();
  },
  async deleteForecast(investmentId, forecastId) {
    const res = await fetch(`${API_BASE_URL}${FORECASTS_PATH}/${investmentId}/${forecastId}`, {
      method: "DELETE" });
    if (!res.ok) throw new Error("Failed to delete forecast, res status: " + res.status);
    return res.json();
  },
};
