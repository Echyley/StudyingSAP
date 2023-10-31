import { IdWithLabel, LocalizedMap } from 'smarteditcommons';
import { SelectorItemThumbnail } from '../types';
export interface PopulatorItem extends IdWithLabel {
    uid: string;
    catalogId: string;
    catalogVersion: string;
    code: string;
    description: LocalizedMap;
    name: LocalizedMap;
    thumbnail: SelectorItemThumbnail;
}
