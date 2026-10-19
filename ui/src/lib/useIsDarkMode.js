import { useEffect, useState } from "react";

const readIsDark = () => typeof document !== "undefined" && document.documentElement.classList.contains("dark");

/**
 * Tracks whether the "dark" class is present on the html element, so charts
 * re-render when the theme toggle is used.
 *
 * @returns {boolean} true when the dark theme is active
 */
export function useIsDarkMode() {
    const [isDark, setIsDark] = useState(readIsDark);

    useEffect(() => {
        const observer = new MutationObserver(() => setIsDark(readIsDark()));
        observer.observe(document.documentElement, { attributes: true, attributeFilter: ["class"] });
        setIsDark(readIsDark());
        return () => observer.disconnect();
    }, []);

    return isDark;
}
