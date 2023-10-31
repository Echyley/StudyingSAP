import { ElementRef } from '@angular/core';
import { ModalFullScreenButtonService } from './ModalFullScreenButtonService';
export declare class ModalFullScreenButtonComponent {
    private element;
    private modalFullScreenButtonService;
    isFullscreen: boolean;
    constructor(element: ElementRef, modalFullScreenButtonService: ModalFullScreenButtonService);
    toggle(state?: boolean): void;
}
