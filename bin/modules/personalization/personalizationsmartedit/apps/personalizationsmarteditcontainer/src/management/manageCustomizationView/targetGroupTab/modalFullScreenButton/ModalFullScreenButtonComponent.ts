/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, ElementRef } from '@angular/core';
import { ModalFullScreenButtonService } from './ModalFullScreenButtonService';

@Component({
    selector: 'perso-modal-full-screen-button',
    templateUrl: './ModalFullScreenButtonComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ModalFullScreenButtonService]
})
export class ModalFullScreenButtonComponent {
    public isFullscreen: boolean;

    constructor(
        private element: ElementRef,
        private modalFullScreenButtonService: ModalFullScreenButtonService
    ) {
        this.isFullscreen = false;
    }

    public toggle(state?: boolean): void {
        this.isFullscreen = typeof state === 'undefined' ? !this.isFullscreen : state;
        this.modalFullScreenButtonService.toggleClassOnCustomizationModal(
            this.element.nativeElement,
            'modal-fullscreen',
            this.isFullscreen
        );
    }
}
