import { RendererFactory2 } from '@angular/core';
export declare class ModalFullScreenButtonService {
    private readonly className;
    private readonly renderer;
    constructor(rendererFactory: RendererFactory2);
    toggleClassOnCustomizationModal(childElement: Element, classNameToToggle: string, toggleState: boolean): void;
    private findModal;
}
