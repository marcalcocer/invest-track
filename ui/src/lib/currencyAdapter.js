/**
 * Adapts an amount to a specific currency format using Spanish (es-ES) conventions.
 * Spanish convention: Dot for thousands, comma for decimals.
 */
export const currencyAdapter = (amount, currency) => {
    if (currency == null) currency = "EUR";
    const value = amount ? (typeof amount === "number" ? amount : parseFloat(amount)) : 0;

    return new Intl.NumberFormat('es-ES', {
        style: 'currency',
        currency: currency,
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(value);
};

/**
 * Formats a plain number using Spanish (es-ES) conventions.
 */
export const numberAdapter = (value, fractionDigits = 2) => {
    const num = value ? (typeof value === "number" ? value : parseFloat(value)) : 0;
    return new Intl.NumberFormat('es-ES', {
        minimumFractionDigits: fractionDigits,
        maximumFractionDigits: fractionDigits
    }).format(num);
};
