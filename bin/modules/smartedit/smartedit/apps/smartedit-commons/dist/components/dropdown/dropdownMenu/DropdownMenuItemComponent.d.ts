import { EventEmitter, Injector, OnInit } from '@angular/core';
import { IDropdownMenuItem } from './IDropdownMenuItem';
export declare class DropdownMenuItemComponent implements OnInit {
    private injector;
    dropdownItem: IDropdownMenuItem;
    selectedItem: any;
    selectedItemChange: EventEmitter<any>;
    dropdownItemInjector: Injector;
    constructor(injector: Injector);
    ngOnInit(): void;
    onClick(): void;
    private createDropdownItemInjector;
}
