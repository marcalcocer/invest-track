const pad = (value) => String(value).padStart(2, "0");

const toDayMonthYear = (dateObj) =>
    `${pad(dateObj.getDate())}-${pad(dateObj.getMonth() + 1)}-${dateObj.getFullYear()}`;

/**
 * Formats a date as dd-MM-yyyy in local time.
 * @param {Date|string|number} datetime a Date, ISO string or timestamp
 * @returns {string} the formatted date, or an empty string when the input is not a valid date
 */
export const formatDate = (datetime) => {
    const dateObj = new Date(datetime);
    if (Number.isNaN(dateObj.getTime())) {
        return "";
    }
    return toDayMonthYear(dateObj);
};

/**
 * Formats a date as dd-MM-yyyy HH:mm in local time.
 * @param {Date|string|number} datetime a Date, ISO string or timestamp
 * @returns {string} the formatted date and time, or an empty string when the input is not a valid date
 */
export const formatDatetime = (datetime) => {
    const dateObj = new Date(datetime);
    if (Number.isNaN(dateObj.getTime())) {
        return "";
    }
    return `${toDayMonthYear(dateObj)} ${pad(dateObj.getHours())}:${pad(dateObj.getMinutes())}`;
};
