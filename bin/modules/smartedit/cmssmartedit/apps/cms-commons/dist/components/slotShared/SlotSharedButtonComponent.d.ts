import { ChangeDetectorRef, DoCheck, OnDestroy, OnInit } from '@angular/core';
import { ComponentAttributes, CrossFrameEventService, ICatalogService, PopupOverlayConfig, ContextualMenuItemData, IComponentHandlerService, SlotSharedService, IPageInfoService } from 'smarteditcommons';
export declare class SlotSharedButtonComponent implements OnInit, OnDestroy, DoCheck {
    private contextualMenuItem;
    private catalogService;
    private componentHandlerService;
    private crossFrameEventService;
    private pageInfoService;
    private slotSharedService;
    private readonly cdr;
    isExternalSlot: boolean;
    isSlotShared: boolean;
    isGlobalSlot: boolean;
    isPopupOpened: boolean;
    popupConfig: PopupOverlayConfig;
    labelL10nKey: string;
    descriptionL10nKey: string;
    private isPopupOpenedPreviousValue;
    private readonly buttonName;
    private unRegOuterFrameClicked;
    private unRegInnerFrameClicked;
    constructor(contextualMenuItem: ContextualMenuItemData, catalogService: ICatalogService, componentHandlerService: IComponentHandlerService, crossFrameEventService: CrossFrameEventService, pageInfoService: IPageInfoService, slotSharedService: SlotSharedService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    getSlotSharedButtonComponentIconTitle(): string;
    ngOnDestroy(): void;
    ngDoCheck(): void;
    get componentAttributes(): ComponentAttributes;
    get slotId(): string;
    onButtonClick(): void;
    hidePopup(): void;
    replaceSlot(event: MouseEvent): Promise<void>;
    private reload;
}