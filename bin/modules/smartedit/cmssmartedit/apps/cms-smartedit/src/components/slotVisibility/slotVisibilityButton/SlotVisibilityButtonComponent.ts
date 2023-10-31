/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    DoCheck,
    Inject,
    OnDestroy,
    OnInit,
    ViewEncapsulation,
    ViewRef
} from '@angular/core';
import { ICMSComponent, ISlotVisibilityService } from 'cmscommons';
import {
    CrossFrameEventService,
    EVENT_OUTER_FRAME_CLICKED,
    IExperience,
    ISharedDataService,
    PopupOverlayConfig,
    ContextualMenuItemData,
    CONTEXTUAL_MENU_ITEM_DATA
} from 'smarteditcommons';

/**
 * Represents a button image that displays the number of hidden components, as well as a dropdown menu of hidden component.
 * It is used inside the slot contextual menu.
 */
@Component({
    selector: 'se-slot-visibility-button',
    templateUrl: './SlotVisibilityButtonComponent.html',
    styleUrls: ['./SlotVisibilityButtonComponent.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SlotVisibilityButtonComponent implements OnInit, OnDestroy, DoCheck {
    public isPopupOpened: boolean;
    public buttonVisible: boolean;
    public hiddenComponents: ICMSComponent[];
    public hiddenComponentCount?: number;
    public popupConfig: PopupOverlayConfig;

    private readonly buttonName: string;
    private isPopupOpenedPreviousValue: boolean;
    private unRegOuterFrameClicked: () => void;

    constructor(
        @Inject(CONTEXTUAL_MENU_ITEM_DATA) private contextualMenuItem: ContextualMenuItemData,
        private slotVisibilityService: ISlotVisibilityService,
        private sharedDataService: ISharedDataService,
        private crossFrameEventService: CrossFrameEventService,
        private cdr: ChangeDetectorRef
    ) {
        this.isPopupOpened = false;
        this.isPopupOpenedPreviousValue = this.isPopupOpened;
        this.buttonVisible = false;
        this.hiddenComponents = [];
        this.popupConfig = {
            halign: 'left',
            valign: 'bottom',
            additionalClasses: [
                'se-slot-ctx-menu__dropdown-toggle-wrapper',
                'se-slot-ctx-menu__dropdown-toggle-wrapper--slot-visibility',
                'se-slot-ctx-menu__divider'
            ]
        };

        this.buttonName = 'slotVisibilityButton';
    }

    async ngOnInit(): Promise<void> {
        this.unRegOuterFrameClicked = this.crossFrameEventService.subscribe(
            EVENT_OUTER_FRAME_CLICKED,
            () => {
                this.isPopupOpened = false;
            }
        );

        const hiddenComponents = await this.slotVisibilityService.getHiddenComponents(this.slotId);
        const experience = (await this.sharedDataService.get('experience')) as IExperience;

        this.hiddenComponents = this.markExternalComponents(experience, hiddenComponents);

        this.hiddenComponentCount = hiddenComponents.length;
        if (this.hiddenComponentCount > 0) {
            this.buttonVisible = true;
        }

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    ngOnDestroy(): void {
        if (this.unRegOuterFrameClicked) {
            this.unRegOuterFrameClicked();
        }
    }

    ngDoCheck(): void {
        // See ContextualMenuItemData#setRemainOpen comment
        if (this.isPopupOpenedPreviousValue !== this.isPopupOpened) {
            this.isPopupOpenedPreviousValue = this.isPopupOpened;
            this.contextualMenuItem.setRemainOpen(this.buttonName, this.isPopupOpened);
        }
    }

    get slotId(): string {
        return this.contextualMenuItem.componentAttributes.smarteditComponentId;
    }

    public hiddenComponentTrackBy(index: number, _hiddenComponent: ICMSComponent): number {
        return index;
    }

    public toggleMenu(): void {
        this.isPopupOpened = !this.isPopupOpened;
    }

    public hideMenu(): void {
        this.isPopupOpened = false;
    }

    public onInsideClick(event: MouseEvent): void {
        event.stopPropagation();
    }

    getSlotVisibilityButtonTitle(): string {
        return this.buttonVisible
            ? 'se.cms.pagetree.component.menu.icon.hide.title'
            : 'se.cms.pagetree.component.menu.icon.visible.title';
    }

    private markExternalComponents(
        experience: IExperience,
        hiddenComponents: ICMSComponent[]
    ): ICMSComponent[] {
        return hiddenComponents.map((component) => ({
            ...component,
            isExternal: component.catalogVersion !== experience.pageContext.catalogVersionUuid
        }));
    }
}
