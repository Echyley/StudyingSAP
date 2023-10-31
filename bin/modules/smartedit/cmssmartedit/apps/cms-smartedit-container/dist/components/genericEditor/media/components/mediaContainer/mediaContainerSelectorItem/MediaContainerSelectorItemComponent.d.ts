import { ItemComponentData, SelectItem } from 'smarteditcommons';
interface MediaContainerSelectItem extends SelectItem {
    thumbnailUrl: string;
    qualifier: string;
}
export declare class MediaContainerSelectorItemComponent {
    item: MediaContainerSelectItem;
    isSelected: boolean;
    constructor(data: ItemComponentData<MediaContainerSelectItem>);
    getThumbnailUrl(thumbnailUrl: string | undefined): string;
}
export {};
