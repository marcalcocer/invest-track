import { useState, useEffect } from 'react';
import { SunIcon, MoonIcon, ComputerDesktopIcon } from '@heroicons/react/24/outline';
import {
    THEMES,
    THEME_CHANGED_EVENT,
    getStoredTheme,
    setStoredTheme,
    applyTheme,
} from '../lib/theme';

const LABELS = { light: 'Light', dark: 'Dark', system: 'System' };

export default function ThemeToggle() {
    const [theme, setTheme] = useState('system');

    useEffect(() => {
        var stored = getStoredTheme(window.localStorage);
        setTheme(stored);
        applyTheme(stored, document, window.matchMedia.bind(window));
    }, []);

    useEffect(() => {
        if (theme !== 'system') {
            return undefined;
        }
        var media = window.matchMedia('(prefers-color-scheme: dark)');
        var onChange = () => applyTheme('system', document, window.matchMedia.bind(window));
        media.addEventListener('change', onChange);
        return () => media.removeEventListener('change', onChange);
    }, [theme]);

    const cycleTheme = () => {
        var next = THEMES[(THEMES.indexOf(theme) + 1) % THEMES.length];
        setTheme(next);
        setStoredTheme(window.localStorage, next);
        applyTheme(next, document, window.matchMedia.bind(window));
        window.dispatchEvent(new CustomEvent(THEME_CHANGED_EVENT, { detail: next }));
    };

    var label = `Theme: ${LABELS[theme]}`;
    var Icon = theme === 'light' ? SunIcon : theme === 'dark' ? MoonIcon : ComputerDesktopIcon;

    return (
        <button
            onClick={cycleTheme}
            className="p-2 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200 focus:outline-none transition-colors duration-200"
            title={label}
            aria-label={label}
        >
            <Icon className="w-6 h-6" />
        </button>
    );
}
