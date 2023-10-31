import { ItemComponentData, LocalizedMap, SelectItem } from 'smarteditcommons';
interface CategorySelectorItem extends SelectItem {
    name: LocalizedMap;
    code: string;
    catalogId: string;
    catalogVersion: string;
}
export declare class CategorySelectorItemComponent {
    data: ItemComponentData<CategorySelectorItem>;
    item: CategorySelectorItem;
    constructor(data: ItemComponentData<CategorySelectorItem>);
}
export {};
