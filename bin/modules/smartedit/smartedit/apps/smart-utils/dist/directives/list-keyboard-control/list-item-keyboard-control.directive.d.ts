import { ElementRef, OnInit, Renderer2 } from '@angular/core';
/**
 * Directive for marking list item for 'ListKeyboardControlDirective' to allow for navigating with keyboard.
 */
export declare class ListItemKeyboardControlDirective implements OnInit {
    private hostElement;
    private renderer;
    /** @internal */
    private readonly activeClassName;
    constructor(hostElement: ElementRef, renderer: Renderer2);
    /** @internal */
    ngOnInit(): void;
    getElement(): HTMLElement;
    setActive(): void;
    setInactive(): void;
    /** @internal */
    private setTabIndex;
}
