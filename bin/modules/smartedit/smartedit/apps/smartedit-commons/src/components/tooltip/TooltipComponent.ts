/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { AfterViewInit, Component, Input, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { PopoverComponent } from '@fundamental-ngx/core';
import { Subscription } from 'rxjs';
import { Placement } from '../popupOverlay';
import './TooltipComponent.scss';

/**
 * Used to display content in a popover after trigger is applied
 *
 * ### Example
 *
 *      <se-tooltip [triggers]="mouseover">
 *          <span se-tooltip-trigger>Hover me</span>
 *          <p se-tooltip-body>Content</p>
 *      </se-tooltip>
 */
@Component({
    selector: 'se-tooltip',
    template: `
        <fd-popover
            [triggers]="triggers"
            [placement]="placement"
            [appendTo]="appendTo"
            [noArrow]="!isChevronVisible"
            [additionalBodyClass]="'se-tooltip-container'"
            [ngClass]="additionalClasses"
            class="se-tooltip">
            <fd-popover-control>
                <ng-content select="[se-tooltip-trigger]"></ng-content>
            </fd-popover-control>
            <fd-popover-body>
                <div class="popover se-popover">
                    <h3 class="se-popover__title" *ngIf="title">{{ title | translate }}</h3>

                    <div class="se-popover__content">
                        <ng-content select="[se-tooltip-body]"></ng-content>
                    </div>
                </div>
            </fd-popover-body>
        </fd-popover>
    `
})
export class TooltipComponent implements AfterViewInit, OnDestroy {
    /**
     * Array of strings defining what event triggers popover to appear.
     * Accepts any DOM {@link https://www.w3schools.com/jsref/dom_obj_event.asp events}.
     */
    @Input() triggers: string[];
    @Input() placement: Placement;
    @Input() title: string;
    /** The element to which the overlay is attached. By default it is body by fd upgrade */
    @Input() appendTo: ElementRef<any>;
    @Input() isChevronVisible: boolean;
    /** Additional css classes applied to fd-popover element. */
    @Input() additionalClasses: string[];

    @ViewChild(PopoverComponent, { static: false }) popover: PopoverComponent;

    private popoverIsOpenChangeSubscription: Subscription;

    ngAfterViewInit(): void {
        // Ensures the tooltip position is set properly.
        //
        // For some components (such as PageDisplayStatusComponent), there is an issue with Popper.js (used by fundamental-ngx) which causes incorrect calculation of the position.
        // This is a temporary workaround which should be rechallenged when upgrading fundamental-ngx to the newer versions.
        this.popoverIsOpenChangeSubscription = this.popover.isOpenChange.subscribe((isOpen) => {
            if (isOpen) {
                setTimeout(() => this.popover.refreshPosition());
            }
        });
    }

    ngOnDestroy(): void {
        this.popoverIsOpenChangeSubscription.unsubscribe();
    }
}
