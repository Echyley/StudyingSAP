/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectorRef,
    Component,
    DoCheck,
    Inject,
    OnDestroy,
    OnInit,
    ViewRef
} from '@angular/core';
import {
    ComponentAttributes,
    CrossFrameEventService,
    EVENT_OUTER_FRAME_CLICKED,
    EVENT_INNER_FRAME_CLICKED,
    ICatalogService,
    PopupOverlayConfig,
    ContextualMenuItemData,
    CONTEXTUAL_MENU_ITEM_DATA,
    IComponentHandlerService,
    SlotSharedService,
    IPageInfoService
} from 'smarteditcommons';

@Component({
    selector: 'slot-shared-button',
    templateUrl: './SlotSharedButtonComponent.html'
})
export class SlotSharedButtonComponent implements OnInit, OnDestroy, DoCheck {
    public isExternalSlot: boolean;
    /**
     * Is slot shared by current content catalog version?
     * Checks only slots on the page (both page and template slots but not multicountry external slots)
     */
    public isSlotShared: boolean;
    public isGlobalSlot: boolean;
    public isPopupOpened: boolean;
    public popupConfig: PopupOverlayConfig;
    public labelL10nKey: string;
    public descriptionL10nKey: string;

    private isPopupOpenedPreviousValue: boolean;
    private readonly buttonName: string;
    private unRegOuterFrameClicked: () => void;
    private unRegInnerFrameClicked: () => void;

    constructor(
        @Inject(CONTEXTUAL_MENU_ITEM_DATA) private contextualMenuItem: ContextualMenuItemData,
        private catalogService: ICatalogService,
        private componentHandlerService: IComponentHandlerService,
        private crossFrameEventService: CrossFrameEventService,
        private pageInfoService: IPageInfoService,
        private slotSharedService: SlotSharedService,
        private readonly cdr: ChangeDetectorRef
    ) {
        this.popupConfig = {
            halign: 'left',
            valign: 'bottom',
            additionalClasses: [
                'se-slot-ctx-menu__divider',
                'se-slot-ctx-menu__dropdown-toggle-wrapper'
            ]
        };
        this.buttonName = 'slotSharedButton';
        this.isPopupOpened = false;
        this.isPopupOpenedPreviousValue = false;
    }

    async ngOnInit(): Promise<void> {
        // close popup when smarteditcontainer is clicked
        this.unRegOuterFrameClicked = this.crossFrameEventService.subscribe(
            EVENT_OUTER_FRAME_CLICKED,
            () => {
                if (this.crossFrameEventService.isIframe()) {
                    this.hidePopup();
                }
            }
        );

        this.unRegInnerFrameClicked = this.crossFrameEventService.subscribe(
            EVENT_INNER_FRAME_CLICKED,
            () => {
                if (!this.crossFrameEventService.isIframe()) {
                    this.hidePopup();
                }
            }
        );

        this.isExternalSlot = await this.componentHandlerService.isExternalComponent(
            this.slotId,
            this.componentAttributes.smarteditComponentType
        );

        const [
            isSlotShared,
            isCurrentCatalogMultiCountry,
            isGlobalSlot,
            isSameCatalogVersionOfPageAndPageTemplate
        ] = await Promise.all([
            this.slotSharedService.isSlotShared(this.slotId),
            this.catalogService.isCurrentCatalogMultiCountry(),
            this.slotSharedService.isGlobalSlot(
                this.slotId,
                this.componentAttributes.smarteditComponentType
            ),
            this.pageInfoService.isSameCatalogVersionOfPageAndPageTemplate()
        ]);
        // Has the current site with current catalog have any parent catalog
        const isMultiCountry = isCurrentCatalogMultiCountry;

        this.isSlotShared =
            (!isMultiCountry || (isMultiCountry && isSameCatalogVersionOfPageAndPageTemplate)) &&
            !this.isExternalSlot &&
            isSlotShared;

        this.isGlobalSlot = isGlobalSlot;
        this.labelL10nKey = this.isGlobalSlot
            ? 'se.parentslot.decorator.label'
            : 'se.sharedslot.decorator.label';
        this.descriptionL10nKey = this.isGlobalSlot
            ? 'se.cms.slot.shared.parent.popover.message'
            : 'se.cms.slot.shared.popover.message';

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    getSlotSharedButtonComponentIconTitle(): string {
        if (!this.isGlobalSlot) {
            return this.isSlotShared
                ? 'se.cms.pagetree.slot.node.menu.icon.chain.link.title'
                : 'se.cms.pagetree.slot.node.menu.icon.chain.dislink.title';
        }
        return 'se.cms.pagetree.slot.node.menu.icon.chain.globalicon.title';
    }

    ngOnDestroy(): void {
        if (this.unRegOuterFrameClicked) {
            this.unRegOuterFrameClicked();
        }
        if (this.unRegInnerFrameClicked) {
            this.unRegInnerFrameClicked();
        }
    }

    ngDoCheck(): void {
        if (this.isPopupOpenedPreviousValue !== this.isPopupOpened) {
            // See ContextualMenuItemData#setRemainOpen comment
            this.contextualMenuItem.setRemainOpen(this.buttonName, this.isPopupOpened);
            this.isPopupOpenedPreviousValue = this.isPopupOpened;
        }
    }

    get componentAttributes(): ComponentAttributes {
        return this.contextualMenuItem.componentAttributes;
    }

    get slotId(): string {
        return this.componentAttributes.smarteditComponentId;
    }

    public onButtonClick(): void {
        this.isPopupOpened = !this.isPopupOpened;
    }

    public hidePopup(): void {
        this.isPopupOpened = false;
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    public async replaceSlot(event: MouseEvent): Promise<void> {
        event.preventDefault();
        this.hidePopup();

        let replaceSlotPromise: Promise<any>;
        if (this.isGlobalSlot) {
            // Multi Country scenario
            replaceSlotPromise = this.slotSharedService.replaceGlobalSlot(this.componentAttributes);
        } else {
            // Non Multi Country scenario
            replaceSlotPromise = this.slotSharedService.replaceSharedSlot(this.componentAttributes);
        }
        await replaceSlotPromise;

        this.reload();
    }

    private reload(): void {
        this.componentHandlerService.reloadInner();
    }
}
