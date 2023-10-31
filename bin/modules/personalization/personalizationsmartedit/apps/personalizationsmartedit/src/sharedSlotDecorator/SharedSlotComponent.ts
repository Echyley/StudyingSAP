/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* eslint-disable max-classes-per-file */
import {
    ChangeDetectorRef,
    Component,
    Inject,
    OnInit,
    OnChanges,
    OnDestroy,
    SimpleChanges,
    DoCheck
} from '@angular/core';
import {
    PopupOverlayConfig,
    CrossFrameEventService,
    EVENT_OUTER_FRAME_CLICKED,
    ContextualMenuItemData,
    CONTEXTUAL_MENU_ITEM_DATA,
    ComponentAttributes
} from 'smarteditcommons';
import { PersonalizationsmarteditComponentHandlerService } from '../service';
@Component({
    selector: 'perso-shared-slot',
    templateUrl: './SharedSlotComponentTemplate.html'
})
export class SharedSlotComponent implements OnInit, OnChanges, OnDestroy, DoCheck {
    public smarteditComponentId: string;
    public isPopupOpened: boolean;
    public slotSharedFlag: boolean;
    public popupConfig: PopupOverlayConfig;
    private unRegOuterFrameClicked: () => void;
    private isPopupOpenedPreviousValue: boolean;
    private readonly buttonName: string;

    constructor(
        @Inject(CONTEXTUAL_MENU_ITEM_DATA) private contextualMenuItem: ContextualMenuItemData,
        private persoComponentHandlerService: PersonalizationsmarteditComponentHandlerService,
        private crossFrameEventService: CrossFrameEventService,
        private cdr: ChangeDetectorRef
    ) {
        this.popupConfig = {
            halign: 'left',
            valign: 'bottom',
            additionalClasses: [
                'se-slot-ctx-menu__divider',
                'se-slot-ctx-menu__dropdown-toggle-wrapper'
            ]
        };
        this.buttonName = 'SharedSlotButton';
        this.isPopupOpened = false;
        this.isPopupOpenedPreviousValue = false;
    }
    ngOnInit(): void {
        this.slotSharedFlag = false;
        this.persoComponentHandlerService.isSlotShared(this.slotId).then((result: any) => {
            this.slotSharedFlag = result;
        });
        this.unRegOuterFrameClicked = this.crossFrameEventService.subscribe(
            EVENT_OUTER_FRAME_CLICKED,
            () => {
                this.isPopupOpened = false;
            }
        );
        this.cdr.detectChanges();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.active && changes.active.currentValue) {
            this.isPopupOpened = false;
        }
    }

    ngOnDestroy(): void {
        if (this.unRegOuterFrameClicked) {
            this.unRegOuterFrameClicked();
        }
    }

    ngDoCheck(): void {
        if (this.isPopupOpenedPreviousValue !== this.isPopupOpened) {
            // See ContextualMenuItemData#setRemainOpen comment
            this.contextualMenuItem.setRemainOpen(this.buttonName, this.isPopupOpened);
            this.isPopupOpenedPreviousValue = this.isPopupOpened;
        }
    }

    public onButtonClick(): void {
        this.isPopupOpened = !this.isPopupOpened;
    }

    public hidePopup(): void {
        this.isPopupOpened = false;
    }

    get componentAttributes(): ComponentAttributes {
        return this.contextualMenuItem.componentAttributes;
    }

    get slotId(): string {
        return this.componentAttributes.smarteditComponentId;
    }
}
