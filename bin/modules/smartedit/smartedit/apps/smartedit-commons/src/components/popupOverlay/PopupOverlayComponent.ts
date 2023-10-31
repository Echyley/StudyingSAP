/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces useValue:false */
import {
    Component,
    EventEmitter,
    Injector,
    Input,
    Output,
    SimpleChanges,
    ViewChild,
    ViewEncapsulation
} from '@angular/core';
import { PopoverComponent } from '@fundamental-ngx/core';
import { TypedMap } from '@smart/utils';

import { PopupOverlayConfig, POPUP_OVERLAY_DATA } from './PopupOverlayConfig';
import { PopoverTrigger, PopupOverlayTrigger, Placement } from './PopupOverlayTrigger';

/**
 *  Component that allows popups/overlays to be displayed attached to any element.
 *  The element that wrapped with PopupOverlay is applied to is called the anchor element. Once the popup is displayed, it is
 *  positioned relative to the anchor, depending on the configuration provided.
 *
 *  ### Example
 *
 *      <se-popup-overlay
 *          [popupOverlay]="config"
 *          [popupOverlayTrigger]="true"
 *          [popupOverlayData]={ item: item }
 *          (popupOverlayOnShow)="handlePopupOverlayDisplayed()"
 *      >
 *          <se-anchor></se-anchor>
 *      </se-popup-overlay>
 *
 *  Content projection may be used instead PopupOverlayConfig body templates. Requires to set "se-popup-overlay-body" attribute on the element to be projected.
 *
 *  ### Example
 *
 *      <se-popup-overlay
 *          [popupOverlay]="config"
 *          [popupOverlayTrigger]="true"
 *          [popupOverlayData]={ item: item }
 *          (popupOverlayOnShow)="handlePopupOverlayDisplayed()"
 *      >
 *          <se-anchor></se-anchor>
 *          <div se-popup-overlay-body>Popover Body</div>
 *      </se-popup-overlay>
 *
 */
@Component({
    selector: 'se-popup-overlay',
    templateUrl: './PopupOverlayComponent.html',
    styleUrls: ['./PopupOverlayComponent.scss'],
    encapsulation: ViewEncapsulation.None
})
export class PopupOverlayComponent {
    /**
     * Configuration object that may only contain one of the body templates. Either template or templateUrl or component.
     *
     * Content projection may be used instead. See an example in the component description.
     */
    @Input() popupOverlay: PopupOverlayConfig;

    /**
     * Controls when the overlay is displayed.
     *
     * If true, the overlay is displayed, if false (or something other then true or click)
     * then the overlay is hidden.
     *
     * If 'click' then the overlay is displayed when the anchor (element) is clicked on.
     */
    @Input() popupOverlayTrigger: PopupOverlayTrigger;

    /**
     * An object that is accessible within Angular component scope
     * can be injected with {@link POPUP_OVERLAY_DATA} token.
     */
    @Input() popupOverlayData: TypedMap<any>;

    @Output() popupOverlayOnShow: EventEmitter<any> = new EventEmitter();

    @Output() popupOverlayOnHide: EventEmitter<any> = new EventEmitter();

    @ViewChild(PopoverComponent, { static: false }) popover: PopoverComponent;

    public isOpen: boolean;
    public trigger: string[] = [];
    public popupOverlayInjector: Injector;

    constructor(private injector: Injector) {}

    ngOnInit(): void {
        this.createInjector();
        this.setTrigger();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.popupOverlayTrigger) {
            this.setTrigger();
        }

        if (changes.popupOverlayData) {
            this.createInjector();
        }
    }

    public handleOpenChange(isOpen: boolean): void {
        return isOpen ? this.handleOpen() : this.handleClose();
    }

    public handleOpen(): void {
        this.isOpen = true;
        this.popupOverlayOnShow.emit();
    }

    public handleClose(): void {
        this.isOpen = false;
        this.popupOverlayOnHide.emit();
    }

    public getPlacement(): Placement {
        return `${this.popupOverlay.valign || 'bottom'}-${this.getHorizontalAlign()}` as Placement;
    }

    public updatePosition(): void {
        this.popover.refreshPosition();
    }

    private setTrigger(): void {
        if (this.popupOverlayTrigger === PopoverTrigger.Click) {
            this.trigger = [this.popupOverlayTrigger];
        }

        if (this.popupOverlayTrigger === PopoverTrigger.Hover) {
            this.trigger = ['mouseenter', 'mouseleave'];
        }

        if (!this.popover) {
            return;
        }

        if (this.popupOverlayTrigger === 'true' || this.popupOverlayTrigger === true) {
            this.popover.open();
        } else {
            this.popover.close();
        }
    }

    private getHorizontalAlign(): 'start' | 'end' {
        if (!this.popupOverlay.halign) {
            return 'start';
        }

        return this.popupOverlay.halign === 'right' ? 'start' : 'end';
    }

    private createInjector(): void {
        this.popupOverlayInjector = Injector.create({
            providers: [
                {
                    provide: POPUP_OVERLAY_DATA,
                    useValue: this.popupOverlayData || {}
                }
            ],
            parent: this.injector
        });
    }
}
