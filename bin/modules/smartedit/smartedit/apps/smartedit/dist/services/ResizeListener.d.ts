export declare class ResizeListener {
    private _resizeObserver;
    private resizeListenersRegistry;
    constructor();
    /**
     * registers a resize listener of a given node
     */
    register(element: HTMLElement, listener: () => void): void;
    /**
     * unregisters listeners on all nodes and cleans up
     */
    dispose(): void;
    /**
     * unregisters the resize listener of a given node
     */
    unregister(element: HTMLElement): void;
    _listenerCount(): number;
}
