import { SystemEventService, ItemComponentData } from 'smarteditcommons';
import { CMSLinkItem } from '../../../genericEditor/singleActiveCatalogAwareSelector';
export interface CMSLinkDropdownItem extends CMSLinkItem {
    id: string;
}
export declare class CmsDropdownItemComponent {
    private systemEventService;
    item: CMSLinkDropdownItem;
    isSelected: boolean;
    qualifier: string;
    constructor(data: ItemComponentData<CMSLinkDropdownItem>, systemEventService: SystemEventService);
    onClick(event: Event): void;
}
