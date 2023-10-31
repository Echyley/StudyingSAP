export declare abstract class IResizeListener {
    unregister(element: HTMLElement): void;
    fix(element: HTMLElement): void;
    register(element: HTMLElement, listener: (element: HTMLElement) => void): void;
    init(): void;
    dispose(): void;
}
