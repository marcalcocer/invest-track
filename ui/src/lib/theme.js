export const THEME_STORAGE_KEY = 'theme';
export const THEME_CHANGED_EVENT = 'themeChanged';
export const THEMES = ['light', 'dark', 'system'];

export function normalizeTheme(value) {
    return THEMES.includes(value) ? value : 'system';
}

export function resolveTheme(preference, systemPrefersDark) {
    var normalized = normalizeTheme(preference);
    if (normalized === 'system') {
        return systemPrefersDark ? 'dark' : 'light';
    }
    return normalized;
}

export function getStoredTheme(storage) {
    try {
        return normalizeTheme(storage.getItem(THEME_STORAGE_KEY));
    } catch (error) {
        return 'system';
    }
}

export function setStoredTheme(storage, value) {
    var normalized = normalizeTheme(value);
    try {
        storage.setItem(THEME_STORAGE_KEY, normalized);
    } catch (error) {
        // Storage may be unavailable (private mode); the preference still applies for this page view.
    }
    return normalized;
}

export function applyTheme(preference, doc, matchMedia) {
    var prefersDark = false;
    if (typeof matchMedia === 'function') {
        prefersDark = Boolean(matchMedia('(prefers-color-scheme: dark)').matches);
    }
    var resolved = resolveTheme(preference, prefersDark);
    var root = doc.documentElement;
    if (resolved === 'dark') {
        root.classList.add('dark');
    } else {
        root.classList.remove('dark');
    }
    root.style.colorScheme = resolved;
    return resolved;
}
