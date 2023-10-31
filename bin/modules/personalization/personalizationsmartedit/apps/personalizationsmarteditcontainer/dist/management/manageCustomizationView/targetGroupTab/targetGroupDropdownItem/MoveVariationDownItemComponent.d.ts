import { OnInit } from '@angular/core';
import { IDropdownMenuItemData } from 'smarteditcommons';
import { TargetGroupTabComponent } from '../TargetGroupTabComponent';
export declare class MoveVariationDownItemComponent implements OnInit {
    private targetGroupTabComponent;
    isLast: boolean;
    private selectedItem;
    constructor(dropdownMenuItemData: IDropdownMenuItemData, targetGroupTabComponent: TargetGroupTabComponent);
    ngOnInit(): void;
    moveDown(event: PointerEvent): void;
}
