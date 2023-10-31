import { ChangeDetectorRef, OnInit, OnChanges, OnDestroy, SimpleChanges, DoCheck } from '@angular/core';
import { PopupOverlayConfig, CrossFrameEventService, ContextualMenuItemData, ComponentAttributes } from 'smarteditcommons';
import { PersonalizationsmarteditComponentHandlerService } from '../service';
export declare class SharedSlotComponent implements OnInit, OnChanges, OnDestroy, DoCheck {
    private contextualMenuItem;
    private persoComponentHandlerService;
    private crossFrameEventService;
    private cdr;
    smarteditComponentId: string;
    isPopupOpened: boolean;
    slotSharedFlag: boolean;
    popupConfig: PopupOverlayConfig;
    private unRegOuterFrameClicked;
    private isPopupOpenedPreviousValue;
    private readonly buttonName;
    constructor(contextualMenuItem: ContextualMenuItemData, persoComponentHandlerService: PersonalizationsmarteditComponentHandlerService, crossFrameEventService: CrossFrameEventService, cdr: ChangeDetectorRef);
    ngOnInit(): void;
    ngOnChanges(changes: SimpleChanges): void;
    ngOnDestroy(): void;
    ngDoCheck(): void;
    onButtonClick(): void;
    hidePopup(): void;
    get componentAttributes(): ComponentAttributes;
    get slotId(): string;
}
