import React from 'react';
import './ConfirmDialog.scss';

interface ConfirmDialogProps {
    isOpen: boolean;
    message: string;
    onConfirm: () => void;
    onCancel: () => void;
}

const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
                                                         isOpen,
                                                         message,
                                                         onConfirm,
                                                         onCancel,
                                                     }) => {
    if (!isOpen) return null;

    return (
        <div className="confirm-overlay">
            <div className="confirm-dialog">
                <p className="confirm-message">{message}</p>
                <div className="confirm-buttons">
                    <button className="btn btn-cancel" onClick={onCancel}>
                        Annulla
                    </button>
                    <button className="btn btn-confirm" onClick={onConfirm}>
                        Conferma
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmDialog;