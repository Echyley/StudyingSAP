/// <reference types="lodash" />
import { BootstrapPayload, LogService } from 'smarteditcommons';
declare global {
    interface InternalSmartedit {
        SmarteditFactory: (payload: BootstrapPayload) => any;
    }
    interface Window {
        smartedit: SmarteditNamespace;
    }
}
export interface SmarteditNamespace {
    addOnReprocessPageListener: (callback: () => void) => void;
    reprocessPage: () => void;
    applications: string[];
    domain: string;
    smarteditroot: string;
    renderComponent?: (componentId: string, componentType?: string, parentId?: string) => any;
}
export declare class SeNamespaceService {
    private logService;
    reprocessPage: import("lodash").DebouncedFunc<any>;
    constructor(logService: LogService);
    renderComponent(componentId: string, componentType?: string, parentId?: string): boolean;
    private _reprocessPage;
    private get namespace();
}
