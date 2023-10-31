import { ItemComponentData, SelectItem } from 'smarteditcommons';
interface SingleActiveCatalogAwareItem extends SelectItem {
    code: string;
    catalogId: string;
}
export declare class SingeActiveCatalogAwareItemSelectorItemRendererComponent {
    data: ItemComponentData<SingleActiveCatalogAwareItem>;
    constructor(data: ItemComponentData<SingleActiveCatalogAwareItem>);
}
export {};
