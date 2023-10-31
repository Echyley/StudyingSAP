/// <reference types="jquery" />
import { AfterViewInit, OnDestroy, OnChanges, SimpleChanges, TemplateRef, ViewContainerRef } from '@angular/core';
export declare class ScrollZoneComponent implements AfterViewInit, OnChanges, OnDestroy {
    private viewContainerRef;
    private document;
    scrollZoneVisible: boolean | undefined;
    isTransparent: boolean;
    elementToScroll: JQuery<HTMLElement>;
    scrollZoneBody: TemplateRef<any>;
    scrollZoneTop: boolean;
    scrollZoneBottom: boolean;
    private viewRef;
    private isScrolling;
    private scrollZoneTopTimeout;
    private scrollZoneBottomTimeout;
    constructor(viewContainerRef: ViewContainerRef, document: Document);
    ngAfterViewInit(): void;
    ngOnChanges(changes: SimpleChanges): void;
    ngOnDestroy(): void;
    onScrollTopMouseenter(): void;
    onScrollBottomMouseenter(): void;
    stopScroll(): void;
    private scrollTop;
    private scrollBottom;
    private createScrollZoneBody;
    private destroyScrollZoneBody;
}
