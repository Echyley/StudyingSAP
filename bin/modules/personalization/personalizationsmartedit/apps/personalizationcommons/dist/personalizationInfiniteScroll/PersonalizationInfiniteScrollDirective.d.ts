import { ElementRef, EventEmitter, OnDestroy, OnInit } from '@angular/core';
export declare class PersonalizationInfiniteScrollDirective implements OnInit, OnDestroy {
    element: ElementRef;
    scrollPercent: number;
    onScrollAction: EventEmitter<any>;
    private scrollEvent;
    private subscription;
    constructor(element: ElementRef);
    ngOnInit(): void;
    ngOnDestroy(): void;
}
