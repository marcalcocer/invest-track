export const THEME_STORAGE_KEY = "theme";

const VALID_THEMES = ["light", "dark"];

/**
 * Reads the persisted theme, ignoring missing, invalid or unreadable values.
 *
 * @param {{getItem: function(string): (string|null)}} storage a localStorage-like object
 * @returns {"light"|"dark"|null} the stored theme or null
 */
export function getStoredTheme(storage) {
    try {
        var value = storage.getItem(THEME_STORAGE_KEY);
        return VALID_THEMES.includes(value) ? value : null;
    } catch {
        return null;
    }
}

/**
 * Decides which theme to use: the stored one wins, otherwise the system preference.
 *
 * @param {"light"|"dark"|null} stored the persisted theme
 * @param {boolean} prefersDark whether the system prefers a dark scheme
 * @returns {"light"|"dark"} the resolved theme
 */
export function resolveTheme(stored, prefersDark) {
    if (VALID_THEMES.includes(stored)) {
        return stored;
    }
    return prefersDark ? "dark" : "light";
}

/**
 * Applies a theme by toggling the "dark" class on the root element and persists it.
 *
 * @param {"light"|"dark"} theme the theme to apply
 * @param {{classList: {toggle: function(string, boolean)}}} root the document element
 * @param {{setItem: function(string, string)}} [storage] optional storage used to persist the choice
 */
export function applyTheme(theme, root, storage) {
    root.classList.toggle("dark", theme === "dark");
    if (storage) {
        try {
            storage.setItem(THEME_STORAGE_KEY, theme);
        } catch {
            // Persistence is best effort: the theme is still applied for this session.
        }
    }
}
