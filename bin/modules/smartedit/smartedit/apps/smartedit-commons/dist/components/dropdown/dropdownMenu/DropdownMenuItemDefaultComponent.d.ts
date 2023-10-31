import { InjectionToken } from '@angular/core';
import { IDropdownMenuItem } from './IDropdownMenuItem';
export interface IDropdownMenuItemData {
    dropdownItem: IDropdownMenuItem;
    selectedItem: any;
}
export declare const DROPDOWN_MENU_ITEM_DATA: InjectionToken<IDropdownMenuItemData>;
export declare class DropdownMenuItemDefaultComponent {
    data: IDropdownMenuItemData;
    constructor(data: IDropdownMenuItemData);
}
