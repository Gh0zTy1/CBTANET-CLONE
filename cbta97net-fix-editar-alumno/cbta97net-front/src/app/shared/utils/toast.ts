import { toast } from 'ngx-sonner';

export function toastSuccess(title: string, description?: string): void {
    toast.success(title, {
        description: description,
        position: "top-right",
        classes: {
            toast: '!flex !align-top !bg-white border-gray-300 !border-l-4 !border-l-(--succes)',
            title: '!text-(length:--text-sm) !text-(--succes)',
            description: '!text-gray-600',
        }
    });
}

export function toastWarning(title: string, description?: string): void {
    toast.warning(title, {
        description: description,
        position: "top-right",
        icon: undefined,
        classes: {
            toast: '!flex !align-top !bg-white border-gray-300 !border-l-4 !border-l-(--warning)',
            title: '!text-(length:--text-sm) !text-(--warning)',
            description: '!text-gray-600',
            
        }
    });
}

export function toastError(title: string, description?: string): void {
    toast.error(title, {
        description: description,
        position: "top-right",
        classes: {
            toast: '!flex !bg-white !border-1 !border-gray-200 !border-l-4 !border-l-(--destructive)',
            title: '!text-(length:--text-md) !text-(--destructive)',
            description: '!text-gray-600 !text-(length:--text-xs)',
        },
    });
}

export function toastInfo(title: string, description?: string): void {
    toast(title, {
        description,
        position: "top-right",
        classes: {
            toast: 'border border-gray-300 bg-white',
            title: 'text-gray-700',
            description: 'text-gray-600'
        }
    });
}