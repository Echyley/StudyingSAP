import { OnInit } from '@angular/core';
import { IDropdownMenuItemData } from 'smarteditcommons';
import { TargetGroupTabComponent } from '../TargetGroupTabComponent';
export declare class ToggleVariationActiveItemComponent implements OnInit {
    private targetGroupTabComponent;
    label: string;
    private selectedItem;
    constructor(dropdownMenuItemData: IDropdownMenuItemData, targetGroupTabComponent: TargetGroupTabComponent);
    ngOnInit(): void;
    toggle(): void;
    private toggleVariationActive;
}
