import { ChangeDetectorRef, DoCheck, OnDestroy, OnInit } from '@angular/core';
import { ICMSComponent, ISlotVisibilityService } from 'cmscommons';
import { CrossFrameEventService, ISharedDataService, PopupOverlayConfig, ContextualMenuItemData } from 'smarteditcommons';
export declare class SlotVisibilityButtonComponent implements OnInit, OnDestroy, DoCheck {
    private contextualMenuItem;
    private slotVisibilityService;
    private sharedDataService;
    private crossFrameEventService;
    private cdr;
    isPopupOpened: boolean;
    buttonVisible: boolean;
    hiddenComponents: ICMSComponent[];
    hiddenComponentCount?: number;
    popupConfig: PopupOverlayConfig;
    private readonly buttonName;
    private isPopupOpenedPreviousValue;
    private unRegOuterFrameClicked;
    constructor(contextualMenuItem: ContextualMenuItemData, slotVisibilityService: ISlotVisibilityService, sharedDataService: ISharedDataService, crossFrameEventService: CrossFrameEventService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    ngOnDestroy(): void;
    ngDoCheck(): void;
    get slotId(): string;
    hiddenComponentTrackBy(index: number, _hiddenComponent: ICMSComponent): number;
    toggleMenu(): void;
    hideMenu(): void;
    onInsideClick(event: MouseEvent): void;
    getSlotVisibilityButtonTitle(): string;
    private markExternalComponents;
}
