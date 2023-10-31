/// <reference types="jquery" />
import { ChangeDetectorRef, ElementRef, EventEmitter, Injector, OnChanges, DoCheck, SimpleChanges } from '@angular/core';
import { Placement } from '../../popupOverlay';
import { IDropdownMenuItem } from './IDropdownMenuItem';
export declare class DropdownMenuComponent implements OnChanges, DoCheck {
    private cd;
    private yjQuery;
    dropdownItems: IDropdownMenuItem[];
    selectedItem: any;
    selectedItemChange: EventEmitter<any>;
    placement: Placement;
    useProjectedAnchor: boolean;
    isOpen: boolean;
    additionalClasses: string[];
    isOpenChange: EventEmitter<boolean>;
    toggleMenuElement: ElementRef<HTMLDivElement>;
    clonedDropdownItems: IDropdownMenuItem[];
    dropdownMenuItemDefaultInjector: Injector;
    isSubscription: boolean;
    message: string;
    dropdownListSelector: string;
    constructor(cd: ChangeDetectorRef, yjQuery: JQueryStatic);
    clickHandler(event: MouseEvent): void;
    ngOnChanges(changes: SimpleChanges): void;
    ngDoCheck(): void;
    private emitIsOpenChange;
    private setDefaultComponentIfNeeded;
    private validateDropdownItem;
}
