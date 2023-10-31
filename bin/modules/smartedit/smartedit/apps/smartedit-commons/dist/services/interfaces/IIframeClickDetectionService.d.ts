/**
 * Provides the functionality to transmit a click event from anywhere within
 * the contents of the SmartEdit application iFrame to the SmartEdit container. Specifically, the module uses yjQuery to
 * bind mousedown events on the iFrame document to a proxy function, triggering the event on the SmartEdit container.
 */
export declare abstract class IIframeClickDetectionService {
    /**
     * Callback triggered by mousedown event of SmartEdit application
     */
    onIframeClick(): void;
    /**
     * Registers a callback registered given the ID and a callback function
     */
    registerCallback(id: string, callback: () => void): () => void;
    /**
     * Removes a callback registered to the given ID
     */
    removeCallback(id: string): void;
}
