export default function ModalHeader({ title, description, currency, onClose }) {
    return (
        <div className="bg-card p-4 sm:px-6 border-b border-border rounded-t-2xl">
            <div className="flex justify-between items-start">
                <div className="flex-1 min-w-0">
                    <h2 className="text-lg sm:text-xl font-semibold tracking-tight text-foreground truncate">{title}</h2>
                    {description && (
                        <p className="text-muted-foreground text-xs sm:text-sm truncate">
                            {description} {currency && `(${currency})`}
                        </p>
                    )}
                </div>
                <button
                    type="button"
                    aria-label="Close"
                    className="flex-shrink-0 ml-2 h-8 w-8 rounded-lg text-muted-foreground hover:bg-muted hover:text-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring transition-colors"
                    onClick={onClose}
                >
                    ✕
                </button>
            </div>
        </div>
    );
}
