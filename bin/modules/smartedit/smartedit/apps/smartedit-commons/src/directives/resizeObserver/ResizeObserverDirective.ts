/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Directive, ElementRef, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { ResizeObserver, ResizeObserverEntry } from '@juggle/resize-observer';

/**
 * Used to listen to ElementRef resize event.
 *
 * It emits an event once the {@link https://developer.mozilla.org/en-US/docs/Web/API/ResizeObserver ResizeObserver}
 * detects the change.
 *
 * ### Example
 *
 *      <my-custom-component seResizeObserver (onResize)="handleResize()"></my-custom-component>
 */
@Directive({
    selector: '[seResizeObserver]'
})
export class ResizeObserverDirective implements OnInit, OnDestroy {
    @Output() onResize: EventEmitter<void> = new EventEmitter();

    private observer: ResizeObserver;

    constructor(private elementRef: ElementRef) {}

    ngOnInit(): void {
        this.startWatching();
    }

    ngOnDestroy(): void {
        this.observer.disconnect();
    }

    private startWatching(): void {
        this.observer = new ResizeObserver((entries: ResizeObserverEntry[]) =>
            this.internalOnResize(entries)
        );
        this.observer.observe((this.elementRef as any).nativeElement);
    }

    private internalOnResize(entries: ResizeObserverEntry[]): void {
        this.onResize.emit();
    }
}
