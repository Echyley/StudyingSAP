/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, ViewChild } from '@angular/core';
import { PopupOverlayComponent } from './PopupOverlayComponent';
import { PopupOverlayConfig } from './PopupOverlayConfig';

export const ON_SHOW = 'On Show';
@Component({
    template: ` <div>
        <se-popup-overlay
            [popupOverlay]="popupConfig"
            [popupOverlayTrigger]="isPopupOpened"
            (popupOverlayOnHide)="hidePopup()">
            <button type="button" id="clickButton">Click This</button>
        </se-popup-overlay>
        <div id="message">{{ message }}</div>
    </div>`
})
export class TestPopupOverlayFakeComponent {
    @ViewChild(PopupOverlayComponent)
    popupOverlayComponentRef: PopupOverlayComponent;
    public popupConfig: PopupOverlayConfig;
    public isPopupOpened: boolean;
    public message: string;
    public trigger: [];
    constructor() {
        this.popupConfig = {
            halign: 'left',
            valign: 'bottom',
            additionalClasses: ['se-slot-ctx-menu__divider']
        };
        this.isPopupOpened = false;
    }

    setMessage(message: string): void {
        this.message = message;
    }

    hidePopup(): void {
        this.message = '';
    }

    handleOnShow(): void {
        this.message = ON_SHOW;
    }
}
