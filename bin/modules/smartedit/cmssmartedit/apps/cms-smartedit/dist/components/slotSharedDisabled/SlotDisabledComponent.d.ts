import { ChangeDetectorRef, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ComponentAttributes, ICatalogService, L10nPipe, TypedMap, CMSModesService, SlotSharedService } from 'smarteditcommons';
interface PopoverMessage {
    translate: string;
    translateParams: {
        catalogName: string;
        slotId: string;
    };
}
export declare class SlotDisabledComponent implements OnInit, OnChanges {
    private catalogService;
    private cMSModesService;
    private l10nPipe;
    private slotSharedService;
    private cdr;
    componentAttributes: ComponentAttributes;
    active: boolean;
    popoverMessage?: PopoverMessage;
    iconClass?: string;
    outerSlotClass?: TypedMap<boolean>;
    isGlobalSlot: boolean;
    isVersioningPerspective: boolean;
    private readonly DEFAULT_DECORATOR_MSG;
    private readonly GLOBAL_SLOT_DECORATOR_MSG;
    private readonly DEFAULT_DECORATOR_MSG_VERSIONING_MODE;
    private readonly GLOBAL_SLOT_DECORATOR_MSG_VERSIONING_MODE;
    constructor(catalogService: ICatalogService, cMSModesService: CMSModesService, l10nPipe: L10nPipe, slotSharedService: SlotSharedService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    ngOnChanges(changes: SimpleChanges): void;
    private getPopoverMessage;
    private setIconAndOuterSlotClassName;
    private getOuterSlotClass;
}
export {};
