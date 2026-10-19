/**
 * Joins class names, skipping falsy values. Dependency-free stand-in for clsx.
 *
 * @param {...(string|false|null|undefined)} inputs class name candidates
 * @returns {string} the space-separated class list
 */
export function cn(...inputs) {
    return inputs.filter(Boolean).join(" ");
}
