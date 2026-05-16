export default function BaseModal({ children, onClose, maxWidth = "max-w-4xl" }) {
    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-2 sm:p-4 z-50 overflow-y-auto" onClick={onClose}>
            <div 
                className={`bg-white rounded-lg shadow-lg w-full ${maxWidth} max-h-[90vh] overflow-y-auto mx-2 relative`}
                onClick={(e) => e.stopPropagation()}
            >
                {children}
            </div>
        </div>
    );
}
