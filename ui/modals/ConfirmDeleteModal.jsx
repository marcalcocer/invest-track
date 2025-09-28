import React from 'react';

export default function ConfirmDeleteModal({ entity, id, name, onCancel, onConfirm }) {
    return (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex justify-center items-center">
            <div className="bg-white p-6 rounded-lg shadow-lg w-96 text-center">
                <h2 className="text-xl font-semibold mb-4">Delete {entity.charAt(0).toUpperCase() + entity.slice(1)}</h2>
                <p className="text-gray-700 mb-6">
                    Are you sure you want to delete the {entity} "<strong>{name}</strong>"?<br /><br />
                    This action cannot be undone.
                </p>
                <div className="flex justify-center space-x-2">
                    <button className="px-4 py-2 bg-gray-400 text-white rounded" onClick={onCancel}>Cancel</button>
                    <button className="px-4 py-2 bg-red-500 text-white rounded" onClick={() => onConfirm(id)}>Delete</button>
                </div>
            </div>
        </div>
    );
}
