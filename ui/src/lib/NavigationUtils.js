export const getInvestmentDetailsUrl = (id) => `/invest-track/investment?id=${id}`;

export const navigateToInvestmentDetails = (id) => {
    window.location.href = getInvestmentDetailsUrl(id);
};
