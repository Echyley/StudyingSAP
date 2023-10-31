import { AfterViewInit, ChangeDetectorRef, ElementRef, OnDestroy } from '@angular/core';
import { Nullable } from '../../dtos';
/**
 * Component used to detect whether it's children are vertically overflowing the document.
 *
 * In case of overflow it sets it's max-height to the difference between top position and document bottom, and enables scrolling.
 */
export declare class PreventVerticalOverflowComponent implements AfterViewInit, OnDestroy {
    private readonly document;
    private readonly cdr;
    containerElement: ElementRef<HTMLDivElement>;
    containerStyle: {
        'max-height.px': Nullable<number>;
        overflow: Nullable<string>;
    };
    private observer;
    constructor(document: Document, cdr: ChangeDetectorRef);
    onWindowResize(): void;
    ngAfterViewInit(): void;
    ngOnDestroy(): void;
    private onResize;
    private preventOverflow;
    private preventIfOverflowing;
    private isOverflowing;
    private prevent;
    private reset;
    private getContainerBoundingClientRect;
    private getDocumentHeight;
}
