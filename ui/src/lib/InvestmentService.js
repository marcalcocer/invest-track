import { API_BASE_URL } from "./config";

export const InvestmentService = {
  async fetchInvestments() {
    const answer = await fetch(`${API_BASE_URL}`).then((res) => res.json());
    console.log("Fetched investments", answer);
    return answer;
  },
  async fetchSummary() {
    const answer = await fetch(`${API_BASE_URL}/summary`);
    console.log("Fetched summary", answer);
    return answer.json();
  },
  async createInvestment(investment) {
    const answer = await fetch(`${API_BASE_URL}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(investment),
    });
    if (!answer.ok) {
      throw new Error("status: " + answer.status);
    }

    console.log("Created investment", answer);
    return answer.json();
  },
  async createInvestmentEntry(investmentId, entry) {
    const answer = await fetch(`${API_BASE_URL}/entry/${investmentId}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(entry),
    });

    console.log("Created investment entry", answer);
    return answer.json();
  },
  async deleteInvestment(investmentId) {
    const answer = await fetch(`${API_BASE_URL}/${investmentId}`, {
      method: "DELETE",
    });
    console.log("Deleted investment", answer);
    return answer.json();
  },
  async deleteInvestmentEntry(investmentId, entryId) {
    const answer = await fetch(
      `${API_BASE_URL}/entry/${investmentId}/${entryId}`,
      {
        method: "DELETE",
      }
    );
    console.log("Deleted investment entry", answer);
    return answer.json();
  },

  // Forecast CRUD
  async fetchForecasts(investmentId) {
    const res = await fetch(`${API_BASE_URL}/forecasts/${investmentId}`);
    if (!res.ok) throw new Error("Failed to fetch forecasts");
    return res.json();
  },
  async createForecast(investmentId, forecast) {
    const res = await fetch(`${API_BASE_URL}/forecasts/${investmentId}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(forecast),
    });
    if (!res.ok) throw new Error("Failed to create forecast");
    return res.json();
  },
  async updateForecast(forecastId, forecast) {
    const res = await fetch(`${API_BASE_URL}/forecasts/${forecastId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(forecast),
    });
    if (!res.ok) throw new Error("Failed to update forecast");
    return res.json();
  },
  async deleteForecast(forecastId) {
    const res = await fetch(`${API_BASE_URL}/forecasts/${forecastId}`, {
      method: "DELETE" });
    if (!res.ok) throw new Error("Failed to delete forecast");
    return res.json();
  }
};
