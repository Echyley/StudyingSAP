import { ElementRef, OnDestroy, OnInit } from '@angular/core';
export declare class PersonalizationPreventParentScrollDirective implements OnInit, OnDestroy {
    private element;
    constructor(element: ElementRef);
    mouseWheelEventHandler: (event: any) => void;
    ngOnInit(): void;
    ngOnDestroy(): void;
    private onMouseWheel;
}
