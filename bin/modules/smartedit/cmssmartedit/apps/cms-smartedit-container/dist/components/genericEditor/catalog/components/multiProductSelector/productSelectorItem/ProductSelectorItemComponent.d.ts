import { ItemComponentData, LocalizedMap, SelectItem } from 'smarteditcommons';
import { SelectorItemThumbnail } from '../../../../../genericEditor';
interface ProductSelectorItem extends SelectItem {
    name: LocalizedMap;
    code: string;
    catalogId: string;
    catalogVersion: string;
    thumbnail?: SelectorItemThumbnail;
}
export declare class ProductSelectorItemComponent {
    data: ItemComponentData<ProductSelectorItem>;
    item: ProductSelectorItem;
    private defaultThumbnail;
    constructor(data: ItemComponentData<ProductSelectorItem>);
    getThumbnailUrl(url: string | undefined): string;
}
export {};
