import { useEffect, useState } from "react";
import { getPrivacyMode, PRIVACY_MODE_EVENT } from "@/components/PrivacyToggle";

/**
 * Tracks the privacy mode flag and reacts to changes made with the privacy toggle.
 *
 * @returns {boolean} true when monetary values must be masked
 */
export function usePrivacyMode() {
    const [isPrivate, setIsPrivate] = useState(false);

    useEffect(() => {
        setIsPrivate(getPrivacyMode());

        const handlePrivacyChange = (event) => setIsPrivate(event.detail);

        window.addEventListener(PRIVACY_MODE_EVENT, handlePrivacyChange);
        return () => window.removeEventListener(PRIVACY_MODE_EVENT, handlePrivacyChange);
    }, []);

    return isPrivate;
}
