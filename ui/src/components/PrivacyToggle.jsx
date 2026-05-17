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
            onClick={togglePrivacy}
            className="p-2 text-gray-500 hover:text-gray-700 focus:outline-none transition-colors duration-200"
            title={isPrivate ? "Show values" : "Hide values"}
        >
            {isPrivate ? (
                <EyeSlashIcon className="w-6 h-6" />
            ) : (
                <EyeIcon className="w-6 h-6" />
            )}
        </button>
    );
}
