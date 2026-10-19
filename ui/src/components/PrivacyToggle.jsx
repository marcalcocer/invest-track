import { useState, useEffect } from 'react';
import { EyeIcon, EyeSlashIcon } from '@heroicons/react/24/outline';

export const PRIVACY_MODE_EVENT = 'privacyModeChanged';

export const getPrivacyMode = () => {
    if (typeof window === 'undefined') return false;
    return localStorage.getItem('privacyMode') === 'true';
};

export default function PrivacyToggle() {
    const [isPrivate, setIsPrivate] = useState(false);

    useEffect(() => {
        setIsPrivate(getPrivacyMode());
    }, []);

    const togglePrivacy = () => {
        const newValue = !isPrivate;
        setIsPrivate(newValue);
        localStorage.setItem('privacyMode', String(newValue));
        window.dispatchEvent(new CustomEvent(PRIVACY_MODE_EVENT, { detail: newValue }));
    };

    return (
        <button
            type="button"
            onClick={togglePrivacy}
            className="p-2 rounded-lg text-muted-foreground hover:bg-muted hover:text-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring transition-colors"
            title={isPrivate ? "Show values" : "Hide values"}
            aria-label={isPrivate ? "Show values" : "Hide values"}
            aria-pressed={isPrivate}
        >
            {isPrivate ? (
                <EyeSlashIcon className="w-5 h-5" />
            ) : (
                <EyeIcon className="w-5 h-5" />
            )}
        </button>
    );
}
