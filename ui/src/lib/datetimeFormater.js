const pad = (value) => String(value).padStart(2, "0");

const toDayMonthYear = (dateObj) =>
    `${pad(dateObj.getDate())}-${pad(dateObj.getMonth() + 1)}-${dateObj.getFullYear()}`;

/**
 * Formats a date as dd-MM-yyyy in local time.
 * @param {Date|string|number} datetime a Date, ISO string or timestamp
 * @returns {string} the formatted date
 */
export const formatDate = (datetime) => toDayMonthYear(new Date(datetime));

/**
 * Formats a date as dd-MM-yyyy HH:mm in local time.
 * @param {Date|string|number} datetime a Date, ISO string or timestamp
 * @returns {string} the formatted date and time
 */
export const formatDatetime = (datetime) => {
    const dateObj = new Date(datetime);
    return `${toDayMonthYear(dateObj)} ${pad(dateObj.getHours())}:${pad(dateObj.getMinutes())}`;
};
