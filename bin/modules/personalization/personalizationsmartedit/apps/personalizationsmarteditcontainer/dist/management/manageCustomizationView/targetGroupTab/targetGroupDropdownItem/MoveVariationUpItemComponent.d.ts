import { OnInit } from '@angular/core';
import { IDropdownMenuItemData } from 'smarteditcommons';
import { TargetGroupTabComponent } from '../TargetGroupTabComponent';
export declare class MoveVariationUpItemComponent implements OnInit {
    private targetGroupTabComponent;
    isFirst: boolean;
    private selectedItem;
    constructor(dropdownMenuItemData: IDropdownMenuItemData, targetGroupTabComponent: TargetGroupTabComponent);
    ngOnInit(): void;
    moveUp(event: PointerEvent): void;
}
