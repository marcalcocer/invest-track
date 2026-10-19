import React from 'react';

export default function ConfirmDeleteModal({ entity, id, name, onCancel, onConfirm }) {
    return (
        <div className="fixed inset-0 bg-background/70 backdrop-blur-sm flex justify-center items-center">
            <div className="bg-card text-card-foreground border border-border p-6 rounded-lg shadow-lg w-full max-w-sm mx-2 text-center">
                <h2 className="text-xl font-semibold mb-4">Delete {entity.charAt(0).toUpperCase() + entity.slice(1)}</h2>
                <p className="text-muted-foreground mb-6">
                    Are you sure you want to delete the {entity} "<strong className="text-foreground">{name}</strong>"?<br /><br />
                    This action cannot be undone.
                </p>
                <div className="flex justify-center space-x-2">
                    <button className="px-4 py-2 bg-muted text-foreground border border-border rounded hover:bg-muted/70" onClick={onCancel}>Cancel</button>
                    <button className="px-4 py-2 bg-danger text-white rounded hover:opacity-90" onClick={() => onConfirm(id)}>Delete</button>
                </div>
            </div>
        </div>
    );
}
