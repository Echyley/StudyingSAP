import { IIframeClickDetectionService } from 'smarteditcommons';
export declare class IframeClickDetectionService extends IIframeClickDetectionService {
    private callbacks;
    constructor();
    registerCallback(id: string, callback: () => void): () => void;
    removeCallback(id: string): boolean;
    /**
     * Triggers all callbacks currently registered to the service. This function is registered as a listener through
     * the GatewayProxy
     */
    onIframeClick(): void;
}
