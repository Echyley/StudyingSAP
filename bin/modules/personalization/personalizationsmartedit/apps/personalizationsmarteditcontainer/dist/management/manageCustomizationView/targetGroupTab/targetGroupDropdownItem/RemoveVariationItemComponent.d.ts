import { IDropdownMenuItemData } from 'smarteditcommons';
import { TargetGroupTabComponent } from '../TargetGroupTabComponent';
export declare class RemoveVariationItemComponent {
    private targetGroupTabComponent;
    private selectedItem;
    constructor(dropdownMenuItemData: IDropdownMenuItemData, targetGroupTabComponent: TargetGroupTabComponent);
    remove(): void;
}
