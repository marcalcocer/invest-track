export const currencyAdapter = (amount, currency) => {
    if (currency == null) currency = "EUR";
    amount = amount ? amount : 0;
    if (typeof amount !== "number") {
        amount = parseFloat(amount);
    }
    amount = amount.toFixed(2);
    if (currency === "EUR") {
        return `${amount}€`;
    } else if (currency === "USD") {
        return `$${amount}`;
    }
    return `${amount} ${currency}`; // Fallback for other currencies
};