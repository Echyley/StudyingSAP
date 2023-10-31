import { IDropdownMenuItemData } from 'smarteditcommons';
import { CustomizationViewService } from '../../CustomizationViewService';
export declare class EditVariationItemComponent {
    private customizationViewService;
    private selectedItem;
    constructor(dropdownMenuData: IDropdownMenuItemData, customizationViewService: CustomizationViewService);
    editVariation(): void;
}
