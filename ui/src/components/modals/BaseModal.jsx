import { useEffect } from 'react';

export default function BaseModal({ children, onClose, maxWidth = "max-w-4xl" }) {
    useEffect(() => {
        const handleKeyDown = (e) => {
            if (e.key === 'Escape') {
                onClose?.();
            }
        };
        const previousOverflow = document.body.style.overflow;
        document.body.style.overflow = 'hidden';
        document.addEventListener('keydown', handleKeyDown);
        return () => {
            document.body.style.overflow = previousOverflow;
            document.removeEventListener('keydown', handleKeyDown);
        };
    }, [onClose]);

    return (
        <div
            className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-2 sm:p-4 z-50 overflow-y-auto"
            onClick={onClose}
        >
            <div
                role="dialog"
                aria-modal="true"
                className={`bg-card text-card-foreground rounded-2xl shadow-2xl border border-border w-full ${maxWidth} max-h-[90vh] overflow-y-auto mx-2 relative animate-fadeIn`}
                onClick={(e) => e.stopPropagation()}
            >
                {children}
            </div>
        </div>
    );
}
