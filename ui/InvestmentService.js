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
  }
};
