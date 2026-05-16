export default function ModalHeader({ title, description, currency, onClose }) {
    return (
        <div className="bg-white p-4 border-b border-gray-200 rounded-t-lg">
            <div className="flex justify-between items-start">
                <div className="flex-1 min-w-0">
                    <h2 className="text-lg sm:text-xl font-bold text-gray-800 truncate">{title}</h2>
                    {description && (
                        <p className="text-gray-600 text-xs sm:text-sm truncate">
                            {description} {currency && `(${currency})`}
                        </p>
                    )}
                </div>
                <button 
                    className="flex-shrink-0 ml-2 text-gray-500 hover:text-gray-800 text-lg"
                    onClick={onClose}
                >
                    ✖
                </button>
            </div>
        </div>
    );
}
