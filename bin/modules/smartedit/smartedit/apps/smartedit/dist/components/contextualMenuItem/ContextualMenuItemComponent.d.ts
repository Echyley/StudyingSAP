import { ElementRef, OnDestroy, OnInit } from '@angular/core';
import { ComponentAttributes, IContextualMenuButton } from 'smarteditcommons';
export declare enum ContextualMenuItemMode {
    Small = "small",
    Compact = "compact"
}
export interface SlotAttributes {
    smarteditSlotId: string;
    smarteditSlotUuid: string;
}
export declare class ContextualMenuItemComponent implements OnInit, OnDestroy {
    private element;
    mode: ContextualMenuItemMode;
    index: number;
    componentAttributes: ComponentAttributes;
    slotAttributes: SlotAttributes;
    itemConfig: IContextualMenuButton;
    classes: string;
    private listeners;
    private modes;
    constructor(element: ElementRef<HTMLElement>);
    ngOnInit(): void;
    ngOnDestroy(): void;
    private validateInput;
    private removeListeners;
    private setupListeners;
}
