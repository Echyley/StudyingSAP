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
    ViewRef
} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    ComponentAttributes,
    CrossFrameEventService,
    EVENT_OUTER_FRAME_CLICKED,
    EVENT_INNER_FRAME_CLICKED,
    ICatalogService,
    IConfirmationModalService,
    IExperience,
    ISharedDataService,
    PopupOverlayConfig,
    MessageType,
    LogService,
    ContextualMenuItemData,
    CONTEXTUAL_MENU_ITEM_DATA,
    IComponentHandlerService,
    IPageInfoService,
    SlotSharedService,
    WindowUtils
} from 'smarteditcommons';
import { SlotUnsharedService } from '../../services/SlotUnsharedService';

@Component({
    selector: 'slot-unshared-button',
    templateUrl: './SlotUnsharedButtonComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SlotUnsharedButtonComponent implements OnInit, OnDestroy, DoCheck {
    public isPopupOpened: boolean;
    public isExternalSlot: boolean;
    public popupConfig: PopupOverlayConfig;
    public removeSlotLinkLabel?: string;
    /**
     * Is slot shared by current content catalog version?
     * Checks only slots on the page (both page and template slots but not multicountry external slots)
     */
    public isSlotShared?: boolean;
    /** Is the slot status OVERRIDE? */
    public isSlotUnshared?: boolean;
    /** Is the current page from the parent catalog. */
    public isCurrentPageFromParent?: boolean;
    /** Has the current site with current catalog have any parent catalog? */
    public isMultiCountry?: boolean;
    public isLocalSlot?: boolean;
    public isNonSharedSlot?: boolean;

    private isPopupOpenedPreviousValue: boolean;
    private readonly buttonName: string;
    /** Used to close popup when outer frame is clicked */
    private unRegOuterFrameClicked: () => void;
    private unRegInnerFrameClicked: () => void;

    constructor(
        @Inject(CONTEXTUAL_MENU_ITEM_DATA) private contextualMenuItem: ContextualMenuItemData,
        private catalogService: ICatalogService,
        private confirmationModalService: IConfirmationModalService,
        private componentHandlerService: IComponentHandlerService,
        private crossFrameEventService: CrossFrameEventService,
        private slotUnsharedService: SlotUnsharedService,
        private sharedDataService: ISharedDataService,
        private slotSharedService: SlotSharedService,
        private translateService: TranslateService,
        private pageInfoService: IPageInfoService,
        private logService: LogService,
        private readonly windowUtils: WindowUtils,
        private cdr: ChangeDetectorRef
    ) {
        this.isPopupOpened = false;
        this.isPopupOpenedPreviousValue = this.isPopupOpened;
        this.popupConfig = {
            halign: 'left',
            valign: 'bottom',
            additionalClasses: [
                'se-slot-ctx-menu__dropdown-toggle-wrapper',
                'se-slot-ctx-menu__divider'
            ]
        };
        this.buttonName = 'slotUnsharedButton';
    }

    async ngOnInit(): Promise<void> {
        this.unRegOuterFrameClicked = this.crossFrameEventService.subscribe(
            EVENT_OUTER_FRAME_CLICKED,
            () => {
                if (this.windowUtils.isIframe()) {
                    this.hidePopup();
                }
            }
        );

        this.unRegInnerFrameClicked = this.crossFrameEventService.subscribe(
            EVENT_INNER_FRAME_CLICKED,
            () => {
                if (!this.windowUtils.isIframe()) {
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
            isSlotUnshared,
            isSameCatalogVersionOfPageAndPageTemplate,
            experience
        ] = await Promise.all([
            this.slotUnsharedService.isSlotShared(this.slotId),
            this.catalogService.isCurrentCatalogMultiCountry(),
            this.slotUnsharedService.isSlotUnshared(this.slotId),
            this.pageInfoService.isSameCatalogVersionOfPageAndPageTemplate(),
            this.sharedDataService.get('experience') as Promise<IExperience>
        ]);
        this.isSlotShared = isSlotShared;

        this.isMultiCountry = isCurrentCatalogMultiCountry;

        this.isSlotUnshared = isSlotUnshared;

        const pageContextCatalogVersionUuid = experience?.pageContext?.catalogVersionUuid || '';

        const catalogDescriptorCatalogVersionUuid =
            experience?.catalogDescriptor?.catalogVersionUuid || '';

        this.isCurrentPageFromParent =
            catalogDescriptorCatalogVersionUuid !== pageContextCatalogVersionUuid;

        this.isLocalSlot =
            this.isMultiCountry &&
            !isSameCatalogVersionOfPageAndPageTemplate &&
            !this.isExternalSlot &&
            !this.isCurrentPageFromParent &&
            this.isSlotShared;

        this.isNonSharedSlot = !this.isExternalSlot && !this.isSlotShared && this.isSlotUnshared;

        this.removeSlotLinkLabel = this.getRemoveSlotLinkLabel();

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    getSlotNotSharedButtonComponentIconTitle(): string {
        return this.isSlotShared
            ? 'se.cms.pagetree.slot.node.menu.icon.chain.link.title'
            : 'se.cms.pagetree.slot.node.menu.icon.chain.dislink.title';
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
        // When I click on button so that the popover is displayed and then I move the cursor outside the decorator, the current decorator dissapears and removes the popover from the DOM.
        // When the popover is opened, the decorator must be displayed unless I click outside.
        if (this.isPopupOpenedPreviousValue !== this.isPopupOpened) {
            this.isPopupOpenedPreviousValue = this.isPopupOpened;
            this.contextualMenuItem.setRemainOpen(this.buttonName, this.isPopupOpened);
        }
    }

    get componentAttributes(): ComponentAttributes {
        return this.contextualMenuItem.componentAttributes;
    }

    get slotId(): string {
        return this.componentAttributes.smarteditComponentId;
    }

    public getHeader(): string {
        return this.isLocalSlot ? 'se.localslot.decorator.label' : 'se.nonshared.decorator.label';
    }

    public toggle(): void {
        this.isPopupOpened = !this.isPopupOpened;
    }

    public hidePopup(): void {
        this.isPopupOpened = false;
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    public async removeSlot($event: Event): Promise<void> {
        $event.preventDefault();
        this.hidePopup();

        const confirmed = await this.confirmationModalService
            .confirm({
                title: this.removeSlotLinkLabel,
                description: this.isLocalSlot
                    ? 'se.cms.local.slot.remove.description'
                    : 'se.cms.slot.remove.description',
                message: {
                    id: 'removeSlotYMessage',
                    text: this.isLocalSlot
                        ? 'se.cms.local.slot.remove.sync.ymessage'
                        : 'se.cms.slot.remove.sync.ymessage',
                    type: MessageType.info
                }
            })
            .catch(() => {
                this.logService.log('Confirmation cancelled');
            });
        if (!confirmed) {
            return;
        }

        // executes the below only when modal has been confirmed
        await this.slotUnsharedService.revertToSharedSlot(
            this.componentAttributes.smarteditComponentUuid
        );

        this.reload();
    }

    public async replaceSlot(event: MouseEvent): Promise<void> {
        event.preventDefault();
        this.hidePopup();

        let replaceSlotPromise: Promise<any>;
        if (this.isLocalSlot) {
            // Non Multi Country scenario
            replaceSlotPromise = this.slotSharedService.replaceSharedSlot(this.componentAttributes);
        }
        await replaceSlotPromise;

        this.reload();
    }
    private getRemoveSlotLinkLabel(): string {
        return this.isLocalSlot
            ? 'se.cms.slot.shared.remove.local.slot.title'
            : 'se.cms.slot.shared.remove.nonshard.slot.title';
    }

    private reload(): void {
        this.componentHandlerService.reloadInner();
    }
}
