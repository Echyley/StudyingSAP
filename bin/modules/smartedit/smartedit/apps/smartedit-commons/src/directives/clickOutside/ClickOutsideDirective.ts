/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { DOCUMENT } from '@angular/common';
import {
    Directive,
    ElementRef,
    EventEmitter,
    HostListener,
    OnDestroy,
    OnInit,
    Output,
    Input,
    Inject
} from '@angular/core';
import { PopoverBodyComponent } from '@fundamental-ngx/core';
import { IIframeClickDetectionService } from '../../services/interfaces/IIframeClickDetectionService';
import { stringUtils } from '../../utils/StringUtils';

@Directive({
    selector: '[seClickOutside]'
})
export class ClickOutsideDirective implements OnInit, OnDestroy {
    @Output() clickOutside: EventEmitter<void> = new EventEmitter<void>();
    @Input() popBody: PopoverBodyComponent;
    private readonly id: string = `clickOutsideIframeClick${stringUtils.generateIdentifier()}`;

    constructor(
        @Inject(DOCUMENT) private document: Document,
        private host: ElementRef,
        private iframeClickDetectionService: IIframeClickDetectionService
    ) {}

    @HostListener('document:click', ['$event.target']) onDocumentClick(target: HTMLElement): void {
        if (
            target === this.host.nativeElement ||
            this.host.nativeElement.contains(target) ||
            (this.popBody &&
                (this.popBody._elementRef.nativeElement.contains(target) ||
                    target === this.popBody._elementRef.nativeElement)) ||
            this.ignoreNotExistingTarget(target)
        ) {
            return;
        }

        this.clickOutside.emit();
    }

    ngOnInit(): void {
        this.iframeClickDetectionService.registerCallback(this.id, () => this.onOutsideClick());
    }

    ngOnDestroy(): void {
        this.iframeClickDetectionService.removeCallback(this.id);
    }

    private ignoreNotExistingTarget(target: HTMLElement): boolean {
        return !this.document.contains(target);
    }

    private onOutsideClick(): void {
        this.clickOutside.emit();
    }
}
